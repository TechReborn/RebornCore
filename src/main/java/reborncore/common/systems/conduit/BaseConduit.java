package reborncore.common.systems.conduit;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Pair;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import reborncore.common.network.ClientBoundPackets;
import reborncore.common.network.NetworkManager;
import reborncore.common.util.IDebuggable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseConduit<T> extends BlockEntity implements Tickable, IDebuggable, IConduit<T> {

	// Thing we move
	protected IConduitTransfer<T> stored = null;
	final Class<? extends IConduitTransfer<T>> transferClass;

	// Neighbouring conduits which are we are connected to
	protected final Map<Direction, IConduit<T>> conduits = new HashMap<>();
	final Class<? extends IConduit<T>> conduitEntityClass;

	// Speciality faces
	protected final FunctionalFaceStorage functionalFaces = new FunctionalFaceStorage();

	private int ticktime = 0;

	// Round robin variable
	private int outputIndex = 0;

	public BaseConduit(BlockEntityType<?> type, Class<? extends IConduit<T>> conduitEntityClass, Class<? extends IConduitTransfer<T>> transferClass) {
		super(type);
		this.transferClass = transferClass;
		this.conduitEntityClass = conduitEntityClass;
	}

	// Abstract functions
	protected abstract void importFace(Direction face);
	protected abstract void exportFace(Direction face);

	protected void onServerLoad(){
		// Conduits populating if read from NBT, note will be conduit type, checked elsewhere
		for(Direction direction : new ArrayList<>(conduits.keySet())){
			if(world == null) return;

			BlockEntity entity = world.getBlockEntity(this.pos.offset(direction));

			if(conduitEntityClass.isInstance(entity)){
				conduits.put(direction, conduitEntityClass.cast(entity));
			}else {
				conduits.remove(direction);
			}
		}
	}

	protected void clientTick(){

	}

	protected void serverTick(){
		if(ticktime == 0){
			onServerLoad();
		}
		ticktime++;

		// Progress item if we have one
		if(stored != null) {
			stored.progress();
		}

		// Loop through each mode and perform action
			for (Map.Entry<Direction, ConduitMode> entry : functionalFaces.getEntrySet()) {

				switch (entry.getValue()) {
					case INPUT:
						if(stored != null) break;
						importFace(entry.getKey());
						break;
					case OUTPUT:
						if(stored == null) break;
						exportFace(entry.getKey());
						break;
					default:
						// No functionality for rest
						break;
				}
			}

		// Item logic after operations
		if(stored != null) {

			// If finished, find another conduit to move to
			if (stored.isFinished()) {

				Pair<IConduit<T>, Direction> destination = getDestinationConduit();

				if (destination != null) {
					// Giving the opposite of the TO direction which is the direction which the new conduit will be facing this entity.
					tryTransferPayload(destination.getRight());
				}
			}
		}

		sync();

	}

	@Override
	public void tick() {
		if(world == null){
			return;
		}

		if(world.isClient()){
			clientTick();
		}else{
			serverTick();
		}
	}

	public void addConduit(Direction direction, IConduit<T> conduit){
		conduits.put(direction, conduit);
	}

	public void removeConduit(Direction direction){
		conduits.remove(direction);
	}

	public void tryTransferPayload(Direction direction){
		if(stored == null){
			return;
		}

		boolean didTransfer = conduits.get(direction).transferPayload(stored, direction.getOpposite());

		if(didTransfer){
			this.stored = null;
		}
	}


	// Called by other conduits
	public boolean transferPayload(IConduitTransfer<T> transfer, Direction origin) {
		if(stored == null) {
			transfer.restartProgress();
			transfer.setOriginDirection(origin);

			this.stored = transfer;

			return true;
		}

		return false;
	}

	// Returns a direction which is next up for transfer
	protected Pair<IConduit<T>, Direction> getDestinationConduit() {
		if (conduits.isEmpty()) {
			return null;
		}

		HashMap<Direction, IConduit<T>> tempConduit = new HashMap<>(conduits);

		// Don't send to where we've received.
		tempConduit.remove(stored.getOriginDirection());

		if(functionalFaces.hasOneWay()){
			Direction oneWayDirection = functionalFaces.getOneWayDirection();

			if(conduits.containsKey(oneWayDirection)){
				return new Pair<>(conduits.get(oneWayDirection),  oneWayDirection);
			}

			// ONE-WAY, FORCED
			return null;
		}

		// If pipe's changed or round robin round is finished, reset index
		if(outputIndex >= tempConduit.size()){
			outputIndex = 0;
		}

		// Round robin crap
		int position = 0;

		for (Map.Entry<Direction, IConduit<T>> entry : tempConduit.entrySet()) {
			if(position == outputIndex) {
				outputIndex++;
				return new Pair<>(entry.getValue(), entry.getKey());
			}

			// Increment if not right index
			position++;
		}

		return null;
	}

	// Returns true if can connect to that direction from this entity
	public boolean canConnect(Direction direction, IConduit<T> otherConduit){

		// Can't connect to direction which has a IO/Block mode
		if(functionalFaces.hasFunctionality(direction) && functionalFaces.getFunctionality(direction) != ConduitMode.ONE_WAY){
			return false;
		}

		return true;
	}

	// Add functionality to side
	public boolean addFunctionality(Direction face, ItemStack playerHolding){
		ConduitMode conduitMode = functionalFaces.getConduitModeFromItemStack(playerHolding);

		if(conduitMode == null) return false;

		// Only one_way can connect
		if(conduitMode != ConduitMode.ONE_WAY){
			if(conduits.containsKey(face)){
				return false;
			}
		}

		if(functionalFaces.canAddFunctionality(face,conduitMode)){
			functionalFaces.addFunctionalFace(face, conduitMode);
			playerHolding.decrement(1);
			return true;
		}

		return false;
	}
	// Add functionality to side
	public ItemStack removeFunctionality(Direction face){
		ConduitMode conduitMode = functionalFaces.getFunctionality(face);

		if(conduitMode == null){
			return ItemStack.EMPTY;
		}

		functionalFaces.removeFunctionality(face);
		return new ItemStack(conduitMode.requiredItem);
	}

	public IConduitTransfer<T> getStored(){
		return stored;
	}


	// Returns a map containing faces which are functional (IO/specials)
	public FunctionalFaceStorage getFunctionalFaces() {
		return functionalFaces;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);

		tag = functionalFaces.toTag(tag);

		if(stored != null) {
			CompoundTag storedTag = new CompoundTag();
			storedTag = stored.toTag(storedTag);
			tag.put("stored", storedTag);
		}

		if(!conduits.isEmpty()){
			ListTag conduitFacesList= new ListTag();

			int index = 0;
			for (Map.Entry<Direction, IConduit<T>> entry : conduits.entrySet()) {
				CompoundTag sidedConduit = new CompoundTag();
				sidedConduit.putInt("direction", entry.getKey().getId());

				conduitFacesList.add(index, sidedConduit);

				index++;
			}

			tag.put("conduit", conduitFacesList);
		}

		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);

		stored = null;
		conduits.clear();

		if(tag.contains("stored")){
			IConduitTransfer<T> transfer = null;
			try {
				transfer = transferClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(transfer != null){
				transfer.fromTag(tag.getCompound("stored"));
				this.stored = transfer;
			}

		}

		functionalFaces.fromTag(tag);

		if(tag.contains("conduit")){
			ListTag conduitList = tag.getList("conduit", NbtType.COMPOUND);

			for (int i = 0; i < conduitList.size(); i++) {
				CompoundTag compoundTag = conduitList.getCompound(i);
				conduits.put(Direction.byId(compoundTag.getInt("direction")), null);

			}
		}
	}

	@Override
	public String getDebugText() {
		String s = "";
		s += IDebuggable.propertyFormat("Conduit count: ", conduits.size() + "\n");
		s += IDebuggable.propertyFormat("Functional faces count", functionalFaces.getSize() + "\n");
		s += IDebuggable.propertyFormat("OutputIndex: ", String.valueOf(outputIndex)) + "\n";

		return s;
	}

	protected void sync() {
		NetworkManager.sendToTracking(ClientBoundPackets.createCustomDescriptionPacket(this), this);
	}
}
