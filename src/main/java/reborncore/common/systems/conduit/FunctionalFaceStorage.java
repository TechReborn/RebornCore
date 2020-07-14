package reborncore.common.systems.conduit;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FunctionalFaceStorage {

	private final Map<Direction, ConduitMode> modeMap = new HashMap<>();

	public void addFunctionalFace(Direction direction, ConduitMode mode){
		modeMap.put(direction, mode);
	}

	public boolean canAddFunctionality(Direction direction, ConduitMode mode){
		if(modeMap.containsKey(direction)) return false;

		int count = (int) modeMap.values().stream().filter(mapMode -> mapMode == mode).count();

		if(count >= mode.maxCount) return false;

		return true;
	}

	public boolean hasFunctionality(Direction face){
		return modeMap.containsKey(face);
	}

	public ConduitMode getFunctionality(Direction face){
		return modeMap.get(face);
	}

	public void removeFunctionality(Direction face){
		modeMap.remove(face);
	}

	public ConduitMode getConduitModeFromItemStack(ItemStack stack){
		for(ConduitMode mode : ConduitMode.values()){
			if(mode.requiredItem == stack.getItem()){
				return mode;
			}
		}

		return null;
	}

	public boolean isEmpty(){
		return modeMap.isEmpty();
	}

	public int getSize(){
		return modeMap.size();
	}

	public boolean hasOneWay(){
		return modeMap.containsValue(ConduitMode.ONE_WAY);
	}
	public Direction getOneWayDirection(){
		for(Map.Entry<Direction, ConduitMode> entry : modeMap.entrySet()){
			if(entry.getValue() == ConduitMode.ONE_WAY){
				return entry.getKey();
			}
		}

		return null;
	}


	public Set<Map.Entry<Direction, ConduitMode>> getEntrySet(){
		return modeMap.entrySet();
	}

	// Loading/Saving

	public void fromTag(CompoundTag tag) {
		modeMap.clear();

		if (tag.contains("functional")) {
			ListTag IOList = tag.getList("functional", NbtType.COMPOUND);

			for (int i = 0; i < IOList.size(); i++) {
				CompoundTag compoundTag = IOList.getCompound(i);

				Direction direction = Direction.byId(compoundTag.getInt("direction"));
				ConduitMode conduitMode = ConduitMode.values()[compoundTag.getInt("mode")];

				modeMap.put(direction, conduitMode);
			}
		}
	}

	public CompoundTag toTag(CompoundTag tag) {
		if(!modeMap.isEmpty()){
			ListTag functionalFacesList = new ListTag();

			int index = 0;
			for (Map.Entry<Direction, ConduitMode> entry : modeMap.entrySet()) {
				CompoundTag sidedIO = new CompoundTag();
				sidedIO.putInt("direction", entry.getKey().getId());
				sidedIO.putInt("mode", entry.getValue().ordinal());

				functionalFacesList.add(index, sidedIO);

				index++;
			}

			tag.put("functional", functionalFacesList);
		}
		return tag;
	}
}
