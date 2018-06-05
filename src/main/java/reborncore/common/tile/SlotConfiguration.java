/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import reborncore.RebornCore;
import reborncore.common.util.Inventory;
import reborncore.common.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SlotConfiguration implements INBTSerializable<NBTTagCompound>{

	List<SlotConfigHolder> slotDetails = new ArrayList<>();

	@Nullable
	Inventory inventory;

	public SlotConfiguration() {
	}

	public SlotConfiguration(Inventory inventory) {
		this.inventory = inventory;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			updateSlotDetails(new SlotConfigHolder(i));
		}
	}

	public void update(TileLegacyMachineBase machineBase){
		if(inventory == null && machineBase.getInventoryForTile().isPresent()){
			inventory = machineBase.getInventoryForTile().get();
		}
		if(inventory != null && slotDetails.size() != inventory.getSizeInventory()){
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				SlotConfigHolder holder = getSlotDetails(i);
				if(holder == null){
					RebornCore.logHelper.debug("Fixed slot " + i + " in " + machineBase);
					//humm somthing has gone wrong
					updateSlotDetails(new SlotConfigHolder(i));
				}
			}
		}
		if(!machineBase.getWorld().isRemote && machineBase.getWorld().getTotalWorldTime() % 5 == 0){
			getSlotDetails().forEach(slotConfigHolder -> slotConfigHolder.handleItemIO(machineBase));
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
		boolean input, output, filter;

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

		private void handleItemIO(TileLegacyMachineBase machineBase){
			if(!input && !output){
				return;
			}
			getAllSides().stream()
				.filter(config -> config.getSlotIO().getIoConfig() != ExtractConfig.NONE)
				.forEach(config -> {
				if(input && config.getSlotIO().getIoConfig() == ExtractConfig.INPUT){
					config.handleItemInput(machineBase);
				}
				if(output && config.getSlotIO().getIoConfig() == ExtractConfig.OUTPUT){
					config.handleItemOutput(machineBase);
				}
			});
		}

		public boolean autoInput() {
			return input;
		}

		public boolean autoOutput() {
			return output;
		}

		public boolean filter() {
			return filter;
		}

		public void setInput(boolean input) {
			this.input = input;
		}

		public void setOutput(boolean output) {
			this.output = output;
		}

		public void setfilter(boolean filter) {
			this.filter = filter;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("slotID", slotID);
			Arrays.stream(EnumFacing.VALUES).forEach(facing -> compound.setTag("side_" + facing.ordinal(), sideMap.get(facing).serializeNBT()));
			compound.setBoolean("input", input);
			compound.setBoolean("output", output);
			compound.setBoolean("filter", filter);
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
			input = nbt.getBoolean("input");
			output = nbt.getBoolean("output");
			if(nbt.hasKey("filter")){ //Was added later, this allows old saves to be upgraded
				filter = nbt.getBoolean("filter");
			}
		}
	}

	public static class SlotConfig implements INBTSerializable<NBTTagCompound> {
		EnumFacing side;
		SlotIO slotIO;
		int slotID;

		public SlotConfig(EnumFacing side, int slotID) {
			this.side = side;
			this.slotID = slotID;
			this.slotIO = new SlotIO(ExtractConfig.NONE);
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

		private void handleItemInput(TileLegacyMachineBase machineBase){
			Inventory inventory = machineBase.getInventoryForTile().get();
			ItemStack targetStack = inventory.getStackInSlot(slotID);
			if(targetStack.getMaxStackSize() == targetStack.getCount()){
				return;
			}
			TileEntity tileEntity = machineBase.getWorld().getTileEntity(machineBase.getPos().offset(side));
			if(tileEntity == null || !tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)){
				return;
			}
			IItemHandler sourceHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			for (int i = 0; i < sourceHandler.getSlots(); i++) {
				ItemStack sourceStack = sourceHandler.getStackInSlot(i);
				if(sourceStack.isEmpty()){
					continue;
				}
				//Checks if we are going to merge stacks that the items are the same
				if(!targetStack.isEmpty()){
					if(!ItemUtils.isItemEqual(sourceStack, targetStack, true, true, false)){
						continue;
					}
				}
				if(!machineBase.isItemValidForSlot(slotID, sourceStack)){
					continue;
				}
				ItemStack extractedStack = sourceHandler.extractItem(i, 4, false);
				if(targetStack.isEmpty()){
					inventory.setInventorySlotContents(slotID, extractedStack);
				} else {
					inventory.getStackInSlot(slotID).grow(extractedStack.getCount());
				}
				inventory.hasChanged = true;
				break;
			}
		}

		private void handleItemOutput(TileLegacyMachineBase machineBase){
			Inventory inventory = machineBase.getInventoryForTile().get();
			ItemStack sourceStack = inventory.getStackInSlot(slotID);
			if(sourceStack.isEmpty()){
				return;
			}
			TileEntity tileEntity = machineBase.getWorld().getTileEntity(machineBase.getPos().offset(side));
			if(tileEntity == null || !tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite())){
				return;
			}
			IItemHandler destHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
			ItemStack stack = ItemHandlerHelper.insertItemStacked(destHandler, sourceStack, false);
			inventory.setInventorySlotContents(slotID, stack);
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

		public SlotIO(NBTTagCompound tagCompound) {
			deserializeNBT(tagCompound);
		}

		public SlotIO(ExtractConfig ioConfig) {
			this.ioConfig = ioConfig;
		}

		public ExtractConfig getIoConfig() {
			return ioConfig;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("config", ioConfig.ordinal());
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			ioConfig = ExtractConfig.values()[nbt.getInteger("config")];
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

	public String toJson(String machineIdent){
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setTag("data", serializeNBT());
		tagCompound.setString("machine", machineIdent);
		return tagCompound.toString();
	}

	public void readJson(String json, String machineIdent) throws UnsupportedOperationException {
		NBTTagCompound compound;
		try {
			compound = JsonToNBT.getTagFromJson(json);
		} catch (NBTException e) {
			throw new UnsupportedOperationException("Clipboard conetents isnt a valid slot configuation");
		}
		if(!compound.hasKey("machine") || !compound.getString("machine").equals(machineIdent)){
			throw new UnsupportedOperationException("Machine config is not for this machine.");
		}
		deserializeNBT(compound.getCompoundTag("data"));
	}
}
