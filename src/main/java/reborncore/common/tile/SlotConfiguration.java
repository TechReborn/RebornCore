package reborncore.common.tile;

import com.sun.javaws.exceptions.InvalidArgumentException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;
import reborncore.common.util.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SlotConfiguration implements INBTSerializable<NBTTagCompound>{

	List<SlotConfigHolder> slotDetails = new ArrayList<>();

	Inventory inventory;

	public SlotConfiguration() {
	}

	public SlotConfiguration(Inventory inventory) {
		this.inventory = inventory;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			updateSlotDetails(new SlotConfigHolder(i));
		}
	}

	public SlotConfiguration(NBTTagCompound tagCompound){
		deserializeNBT(tagCompound);
	}

	public List<SlotConfigHolder> getSlotDetails() {
		return slotDetails;
	}

	/**
	 * Replaces or adds a slot detail for the slot id
	 * @param slotConfigHolder
	 */
	public SlotConfigHolder updateSlotDetails(SlotConfigHolder slotConfigHolder){
		SlotConfigHolder lookup = getSlotDetails(slotConfigHolder.slotID);
		if(lookup != null){
			slotDetails.remove(lookup);
		}
		slotDetails.add(slotConfigHolder);
		return slotConfigHolder;
	}

	@Nullable
	public SlotConfigHolder getSlotDetails(int id){
		for(SlotConfigHolder detail : slotDetails){
			if(detail.slotID == id){
				return detail;
			}
		}
		return null;
	}

	public List<SlotConfig> getSlotsForSide(EnumFacing facing){
		return slotDetails.stream().map(slotConfigHolder -> slotConfigHolder.getSideDetail(facing)).collect(Collectors.toList());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("size", slotDetails.size());
		for (int i = 0; i < slotDetails.size(); i++) {
			tagCompound.setTag("slot_" + i, slotDetails.get(i).serializeNBT());
		}
		return tagCompound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		int size = nbt.getInteger("size");
		for (int i = 0; i < size; i++) {
			NBTTagCompound tagCompound = nbt.getCompoundTag("slot_" + i);
			SlotConfigHolder slotConfigHolder = new SlotConfigHolder(tagCompound);
			updateSlotDetails(slotConfigHolder);
		}
	}

	public static class SlotConfigHolder implements INBTSerializable<NBTTagCompound> {

		int slotID;
		HashMap<EnumFacing, SlotConfig> sideMap;

		public SlotConfigHolder(int slotID) {
			this.slotID = slotID;
			sideMap = new HashMap<>();
			Arrays.stream(EnumFacing.VALUES).forEach(facing -> sideMap.put(facing, new SlotConfig(facing, slotID)));
		}

		public SlotConfigHolder(NBTTagCompound tagCompound) {
			sideMap = new HashMap<>();
			deserializeNBT(tagCompound);
		}

		public SlotConfig getSideDetail(EnumFacing side){
			return sideMap.get(side);
		}

		public List<SlotConfig> getAllSides(){
			return new ArrayList<>(sideMap.values());
		}

		public void updateSlotConfig(SlotConfig config){
			SlotConfig toEdit = sideMap.get(config.side);
			toEdit.slotIO = config.slotIO;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("slotID", slotID);
			Arrays.stream(EnumFacing.VALUES).forEach(facing -> compound.setTag("side_" + facing.ordinal(), sideMap.get(facing).serializeNBT()));
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			sideMap.clear();
			slotID = nbt.getInteger("slotID");
			Arrays.stream(EnumFacing.VALUES).forEach(facing -> {
				NBTTagCompound compound = nbt.getCompoundTag("side_" + facing.ordinal());
				SlotConfig config = new SlotConfig(compound);
				sideMap.put(facing, config);
			});
		}
	}

	public static class SlotConfig implements INBTSerializable<NBTTagCompound> {
		EnumFacing side;
		SlotIO slotIO;
		int slotID;

		public SlotConfig(EnumFacing side, int slotID) {
			this.side = side;
			this.slotID = slotID;
			this.slotIO = new SlotIO(ExtractConfig.NONE, false, false);
		}

		public SlotConfig(EnumFacing side, SlotIO slotIO, int slotID) {
			this.side = side;
			this.slotIO = slotIO;
			this.slotID = slotID;
		}

		public SlotConfig(NBTTagCompound tagCompound) {
			deserializeNBT(tagCompound);
		}

		public EnumFacing getSide() {
			return side;
		}

		public SlotIO getSlotIO() {
			return slotIO;
		}

		public int getSlotID() {
			return slotID;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setInteger("side", side.ordinal());
			tagCompound.setTag("config", slotIO.serializeNBT());
			tagCompound.setInteger("slot", slotID);
			return tagCompound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			side = EnumFacing.VALUES[nbt.getInteger("side")];
			slotIO = new SlotIO(nbt.getCompoundTag("config"));
			slotID = nbt.getInteger("slot");
		}
	}

	public static class SlotIO implements INBTSerializable<NBTTagCompound>{
		ExtractConfig ioConfig;
		boolean input, output;

		public SlotIO(NBTTagCompound tagCompound) {
			deserializeNBT(tagCompound);
		}

		public SlotIO(ExtractConfig ioConfig, boolean input, boolean output) {
			this.ioConfig = ioConfig;
			this.input = input;
			this.output = output;
		}

		public ExtractConfig getIoConfig() {
			return ioConfig;
		}

		public boolean isInput() {
			return input;
		}

		public boolean isOutput() {
			return output;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("config", ioConfig.ordinal());
			compound.setBoolean("input", input);
			compound.setBoolean("output", output);
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			ioConfig = ExtractConfig.values()[nbt.getInteger("config")];
			input = nbt.getBoolean("input");
			output = nbt.getBoolean("output");
		}
	}


	public enum ExtractConfig {
		NONE(false, false),
		INPUT(false, true),
		OUTPUT(true, false);

		boolean extact;
		boolean insert;

		ExtractConfig(boolean extact, boolean insert) {
			this.extact = extact;
			this.insert = insert;
		}

		public boolean isExtact() {
			return extact;
		}

		public boolean isInsert() {
			return insert;
		}

		public ExtractConfig getNext(){
			int i = this.ordinal() + 1;
			if(i >= ExtractConfig.values().length){
				i = 0;
			}
			return ExtractConfig.values()[i];
		}
	}


}
