package reborncore.common.util;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotInput;
import reborncore.client.gui.slots.SlotOutput;
import reborncore.common.container.RebornContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lordmau5 on 16.06.2016.
 */
public class InventoryItemHandler implements IItemHandler {

	private final EnumFacing facing;
	private Map<EnumFacing, List<BaseSlot>> slotMap = new HashMap<>();
	private int slotLimit = 64;

	public InventoryItemHandler(RebornContainer container, EnumFacing facing) {
		this.facing = facing;

		for (EnumFacing _facing : EnumFacing.VALUES) {
			List<BaseSlot> slotList = new ArrayList<>();
			for (Map.Entry<Integer, BaseSlot> entry : container.slotMap.entrySet()) {
				BaseSlot baseSlot = entry.getValue();
				if (_facing == EnumFacing.UP && baseSlot instanceof SlotInput) {
					slotList.add(baseSlot);
				} else if (_facing == EnumFacing.DOWN && baseSlot instanceof SlotOutput) {
					slotList.add(baseSlot);
				} else {
					slotList.add(baseSlot);
				}
			}
			this.slotMap.put(_facing, slotList);
		}
	}

	@Override
	public int getSlots() {
		return slotMap.get(this.facing).size();
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		return this.slotMap.get(this.facing).get(slotIndex).getStack();
	}

	@Override
	public ItemStack insertItem(int slotIndex, ItemStack stack, boolean simulate) {
		if (stack == ItemStack.EMPTY || stack.getCount() == 0)
			return ItemStack.EMPTY;

		Slot slot = this.slotMap.get(this.facing).get(slotIndex);
		if (!slot.getHasStack()) {
			slot.putStack(stack);
			return ItemStack.EMPTY;
		}

		ItemStack existing = slot.getStack();
		int limit = slot.getSlotStackLimit();

		if (existing != ItemStack.EMPTY) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing == ItemStack.EMPTY) {
				slot.putStack(reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.setCount(reachedLimit ? limit : stack.getCount());
			}
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slotIndex, int amount, boolean simulate) {
		if (amount == 0)
			return ItemStack.EMPTY;

		Slot slot = this.slotMap.get(this.facing).get(slotIndex);
		if (slot.getStack() == ItemStack.EMPTY) {
			return ItemStack.EMPTY;
		}

		ItemStack existing = slot.getStack();
		if (existing == ItemStack.EMPTY)
			return ItemStack.EMPTY;

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				slot.putStack(ItemStack.EMPTY);
			}
			return existing;
		} else {
			if (!simulate) {
				slot.putStack(ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return slotLimit;
	}

}
