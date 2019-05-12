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

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.Validate;
import reborncore.RebornCore;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.common.container.RebornContainer;
import reborncore.common.recipes.RecipeCrafter;
import reborncore.common.util.Inventory;
import reborncore.common.util.ItemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SlotConfiguration implements INBTSerializable<CompoundTag> {

	List<SlotConfigHolder> slotDetails = new ArrayList<>();

	@Nullable
	Inventory inventory;

	public SlotConfiguration() {
	}

	public SlotConfiguration(Inventory inventory) {
		this.inventory = inventory;
		//This is done to ensure that the inventory is set to use configured access,
		Validate.isTrue(inventory.configuredAccess);

		for (int i = 0; i < inventory.getSlots(); i++) {
			updateSlotDetails(new SlotConfigHolder(i));
		}
	}

	public void update(TileMachineBase machineBase) {
		if (inventory == null && machineBase.getInventoryForTile().isPresent()) {
			inventory = machineBase.getInventoryForTile().get();
		}
		if (inventory != null && slotDetails.size() != inventory.getSlots()) {
			for (int i = 0; i < inventory.getSlots(); i++) {
				SlotConfigHolder holder = getSlotDetails(i);
				if (holder == null) {
					RebornCore.LOGGER.debug("Fixed slot " + i + " in " + machineBase);
					//humm somthing has gone wrong
					updateSlotDetails(new SlotConfigHolder(i));
				}
			}
		}
		if (!machineBase.getWorld().isClient && machineBase.getWorld().getTime() % machineBase.slotTransferSpeed() == 0) {
			getSlotDetails().forEach(slotConfigHolder -> slotConfigHolder.handleItemIO(machineBase));
		}
	}

	public SlotConfiguration(CompoundTag tagCompound) {
		deserializeNBT(tagCompound);
	}

	public List<SlotConfigHolder> getSlotDetails() {
		return slotDetails;
	}

	/**
	 * Replaces or adds a slot detail for the slot id
	 *
	 * @param slotConfigHolder
	 * @return SlotConfigHolder
	 */
	public SlotConfigHolder updateSlotDetails(SlotConfigHolder slotConfigHolder) {
		SlotConfigHolder lookup = getSlotDetails(slotConfigHolder.slotID);
		if (lookup != null) {
			slotDetails.remove(lookup);
		}
		slotDetails.add(slotConfigHolder);
		return slotConfigHolder;
	}

	@Nullable
	public SlotConfigHolder getSlotDetails(int id) {
		for (SlotConfigHolder detail : slotDetails) {
			if (detail.slotID == id) {
				return detail;
			}
		}
		return null;
	}

	public List<SlotConfig> getSlotsForSide(Direction facing) {
		return slotDetails.stream().map(slotConfigHolder -> slotConfigHolder.getSideDetail(facing)).collect(Collectors.toList());
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tagCompound = new CompoundTag();
		tagCompound.putInt("size", slotDetails.size());
		for (int i = 0; i < slotDetails.size(); i++) {
			tagCompound.put("slot_" + i, slotDetails.get(i).serializeNBT());
		}
		return tagCompound;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		int size = nbt.getInt("size");
		for (int i = 0; i < size; i++) {
			CompoundTag tagCompound = nbt.getCompound("slot_" + i);
			SlotConfigHolder slotConfigHolder = new SlotConfigHolder(tagCompound);
			updateSlotDetails(slotConfigHolder);
		}
	}

	public static class SlotConfigHolder implements INBTSerializable<CompoundTag> {

		int slotID;
		HashMap<Direction, SlotConfig> sideMap;
		boolean input, output, filter;

		public SlotConfigHolder(int slotID) {
			this.slotID = slotID;
			sideMap = new HashMap<>();
			Arrays.stream(Direction.values()).forEach(facing -> sideMap.put(facing, new SlotConfig(facing, slotID)));
		}

		public SlotConfigHolder(CompoundTag tagCompound) {
			sideMap = new HashMap<>();
			deserializeNBT(tagCompound);
			Validate.isTrue(Arrays.stream(Direction.values())
				                .map(enumFacing -> sideMap.get(enumFacing))
				                .noneMatch(Objects::isNull),
			                "sideMap failed to load from nbt"
			);
		}

		public SlotConfig getSideDetail(Direction side) {
			Validate.notNull(side, "A none null side must be used");
			SlotConfig slotConfig = sideMap.get(side);
			Validate.notNull(slotConfig, "slotConfig was null for side " + side);
			return slotConfig;
		}

		public List<SlotConfig> getAllSides() {
			return new ArrayList<>(sideMap.values());
		}

		public void updateSlotConfig(SlotConfig config) {
			SlotConfig toEdit = sideMap.get(config.side);
			toEdit.slotIO = config.slotIO;
		}

		private void handleItemIO(TileMachineBase machineBase) {
			if (!input && !output) {
				return;
			}
			getAllSides().stream()
				.filter(config -> config.getSlotIO().getIoConfig() != ExtractConfig.NONE)
				.forEach(config -> {
					if (input && config.getSlotIO().getIoConfig() == ExtractConfig.INPUT) {
						config.handleItemInput(machineBase);
					}
					if (output && config.getSlotIO().getIoConfig() == ExtractConfig.OUTPUT) {
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
		public CompoundTag serializeNBT() {
			CompoundTag compound = new CompoundTag();
			compound.putInt("slotID", slotID);
			Arrays.stream(Direction.values()).forEach(facing -> compound.put("side_" + facing.ordinal(), sideMap.get(facing).serializeNBT()));
			compound.putBoolean("input", input);
			compound.putBoolean("output", output);
			compound.putBoolean("filter", filter);
			return compound;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			sideMap.clear();
			slotID = nbt.getInt("slotID");
			Arrays.stream(Direction.values()).forEach(facing -> {
				CompoundTag compound = nbt.getCompound("side_" + facing.ordinal());
				SlotConfig config = new SlotConfig(compound);
				sideMap.put(facing, config);
			});
			input = nbt.getBoolean("input");
			output = nbt.getBoolean("output");
			if (nbt.containsKey("filter")) { //Was added later, this allows old saves to be upgraded
				filter = nbt.getBoolean("filter");
			}
		}
	}

	public static class SlotConfig implements INBTSerializable<CompoundTag> {
		@Nonnull
		private Direction side;
		@Nonnull
		private SlotIO slotIO;
		private int slotID;

		public SlotConfig(@Nonnull Direction side, int slotID) {
			this.side = side;
			this.slotID = slotID;
			this.slotIO = new SlotIO(ExtractConfig.NONE);
		}

		public SlotConfig(@Nonnull Direction side, @Nonnull SlotIO slotIO, int slotID) {
			this.side = side;
			this.slotIO = slotIO;
			this.slotID = slotID;
		}

		public SlotConfig(CompoundTag tagCompound) {
			deserializeNBT(tagCompound);
			Validate.notNull(side, "error when loading slot config");
			Validate.notNull(slotIO, "error when loading slot config");
		}

		@Nonnull
		public Direction getSide() {
			Validate.notNull(side);
			return side;
		}

		@Nonnull
		public SlotIO getSlotIO() {
			Validate.notNull(slotIO);
			return slotIO;
		}

		public int getSlotID() {
			return slotID;
		}

		private void handleItemInput(TileMachineBase machineBase) {
			Inventory inventory = machineBase.getInventoryForTile().get();
			ItemStack targetStack = inventory.getStackInSlot(slotID);
			if (targetStack.getMaxAmount() == targetStack.getAmount()) {
				return;
			}
			BlockEntity tileEntity = machineBase.getWorld().getBlockEntity(machineBase.getPos().offset(side));
			if (tileEntity == null || !tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).isPresent()) {
				return;
			}
			IItemHandler sourceHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).orElse(null);
			for (int i = 0; i < sourceHandler.getSlots(); i++) {
				ItemStack sourceStack = sourceHandler.getStackInSlot(i);
				if (sourceStack.isEmpty()) {
					continue;
				}
				//Checks if we are going to merge stacks that the items are the same
				if (!targetStack.isEmpty()) {
					if (!ItemUtils.isItemEqual(sourceStack, targetStack, true, false)) {
						continue;
					}
				}
				int extract = 4;
				if (!targetStack.isEmpty()) {
					extract = Math.min(targetStack.getMaxAmount() - targetStack.getAmount(), extract);
				}
				ItemStack extractedStack = sourceHandler.extractItem(i, extract, false);
				if (targetStack.isEmpty()) {
					inventory.setStackInSlot(slotID, extractedStack);
				} else {
					inventory.getStackInSlot(slotID).grow(extractedStack.getAmount());
				}
				inventory.setChanged();
				break;
			}
		}

		private void handleItemOutput(TileMachineBase machineBase) {
			Inventory inventory = machineBase.getInventoryForTile().get();
			ItemStack sourceStack = inventory.getStackInSlot(slotID);
			if (sourceStack.isEmpty()) {
				return;
			}
			BlockEntity tileEntity = machineBase.getWorld().getBlockEntity(machineBase.getPos().offset(side));
			if (tileEntity == null || !tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).isPresent()) {
				return;
			}
			IItemHandler destHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).orElseGet(null);
			ItemStack stack = ItemHandlerHelper.insertItemStacked(destHandler, sourceStack, false);
			inventory.setStackInSlot(slotID, stack);
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag tagCompound = new CompoundTag();
			tagCompound.putInt("side", side.ordinal());
			tagCompound.put("config", slotIO.serializeNBT());
			tagCompound.putInt("slot", slotID);
			return tagCompound;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			side = Direction.values()[nbt.getInt("side")];
			slotIO = new SlotIO(nbt.getCompound("config"));
			slotID = nbt.getInt("slot");
		}
	}

	public static class SlotIO implements INBTSerializable<CompoundTag> {
		ExtractConfig ioConfig;

		public SlotIO(CompoundTag tagCompound) {
			deserializeNBT(tagCompound);
		}

		public SlotIO(ExtractConfig ioConfig) {
			this.ioConfig = ioConfig;
		}

		public ExtractConfig getIoConfig() {
			return ioConfig;
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag compound = new CompoundTag();
			compound.putInt("config", ioConfig.ordinal());
			return compound;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			ioConfig = ExtractConfig.values()[nbt.getInt("config")];
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

		public ExtractConfig getNext() {
			int i = this.ordinal() + 1;
			if (i >= ExtractConfig.values().length) {
				i = 0;
			}
			return ExtractConfig.values()[i];
		}
	}

	public String toJson(String machineIdent) {
		CompoundTag tagCompound = new CompoundTag();
		tagCompound.put("data", serializeNBT());
		tagCompound.putString("machine", machineIdent);
		return tagCompound.toString();
	}

	public void readJson(String json, String machineIdent) throws UnsupportedOperationException {
		CompoundTag compound;
		try {
			compound = StringNbtReader.parse(json);
		} catch (CommandSyntaxException e) {
			throw new UnsupportedOperationException("Clipboard conetents isnt a valid slot configuation");
		}
		if (!compound.containsKey("machine") || !compound.getString("machine").equals(machineIdent)) {
			throw new UnsupportedOperationException("Machine config is not for this machine.");
		}
		deserializeNBT(compound.getCompound("data"));
	}

	//DO NOT CALL THIS, use the inventory access on the inventory
	public static boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction, TileMachineBase tile) {
		SlotConfiguration.SlotConfigHolder slotConfigHolder = tile.slotConfiguration.getSlotDetails(index);
		SlotConfiguration.SlotConfig slotConfig = slotConfigHolder.getSideDetail(direction);
		if (slotConfig.getSlotIO().getIoConfig().isInsert()) {
			if (slotConfigHolder.filter() && tile.getCrafterForTile().isPresent()) {
				RecipeCrafter crafter = tile.getCrafterForTile().get();
				if (!crafter.isStackValidInput(itemStackIn)) {
					return false;
				}
			}
			if (tile.getContainerForTile().isPresent()) {
				RebornContainer container = tile.getContainerForTile().get();
				if (container.slotMap.containsKey(index)) {
					Slot slot = container.slotMap.get(index);
					return slot.canInsert(itemStackIn);
				}
			} else {
				return true;
			}
		}
		return false;
	}

	//DO NOT CALL THIS, use the inventory access on the inventory
	public static boolean canExtractItem(int index, ItemStack stack, Direction direction, TileMachineBase tile) {
		SlotConfiguration.SlotConfigHolder slotConfigHolder = tile.slotConfiguration.getSlotDetails(index);
		SlotConfiguration.SlotConfig slotConfig = slotConfigHolder.getSideDetail(direction);
		if (slotConfig.getSlotIO().getIoConfig().isExtact()) {
			if (tile.getContainerForTile().isPresent()) {
				RebornContainer container = tile.getContainerForTile().get();
				if (container.slotMap.containsKey(index)) {
					BaseSlot slot = container.slotMap.get(index);
					return slot.canWorldBlockRemove();
				}
			} else {
				return true;
			}
		}
		return false;
	}

}
