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

package reborncore.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.Validate;
import reborncore.api.items.InventoryUtils;
import reborncore.api.items.InventoryWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InventoryItem extends InventoryWrapper {

	@Nonnull
	ItemStack stack;
	int size;

	private InventoryItem(
		@Nonnull
			ItemStack stack, int size) {
		Validate.notNull(stack);
		Validate.isTrue(!stack.isEmpty());
		this.size = size;
		this.stack = stack;
	}

	public static InventoryItem getItemInvetory(ItemStack stack, int size) {
		return new InventoryItem(stack, size);
	}

	public ItemStack getStack() {
		return stack;
	}

	public CompoundTag getInvData() {
		Validate.isTrue(!stack.isEmpty());
		if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
		}
		if (!stack.getTag().containsKey("inventory")) {
			stack.getTag().put("inventory", new CompoundTag());
		}
		return stack.getTag().getCompound("inventory");
	}

	public CompoundTag getSlotData(int slot) {
		validateSlotIndex(slot);
		CompoundTag invData = getInvData();
		if (!invData.containsKey("slot_" + slot)) {
			invData.put("slot_" + slot, new CompoundTag());
		}
		return invData.getCompound("slot_" + slot);
	}

	public void setSlotData(int slot, CompoundTag tagCompound) {
		validateSlotIndex(slot);
		Validate.notNull(tagCompound);
		CompoundTag invData = getInvData();
		invData.put("slot_" + slot, tagCompound);
	}

	public List<ItemStack> getAllStacks() {
		return IntStream.range(0, size)
			.mapToObj(this::getStackInSlot)
			.collect(Collectors.toList());
	}

	public int getSlots() {
		return size;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.fromTag(getSlotData(slot));
	}

	@Override
	public void setStackInSlot(int slot,
	                           @Nonnull
		                           ItemStack stack) {
		setSlotData(slot, stack.toTag(new CompoundTag()));
	}

	//insertItem and extractItem are the forge methods just adjusted to work with items
	@Nonnull
	@Override
	public ItemStack insertItem(int slot,
	                            @Nonnull
		                            ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		validateSlotIndex(slot);
		ItemStack existing = getStackInSlot(slot);
		int limit = getStackLimit(slot, stack);
		if (!existing.isEmpty()) {
			if (!InventoryUtils.canItemStacksStack(stack, existing)) {
				return stack;
			}
			limit -= existing.getAmount();
		}
		if (limit <= 0) {
			return stack;
		}
		boolean reachedLimit = stack.getAmount() > limit;
		if (!simulate) {
			if (existing.isEmpty()) {
				setStackInSlot(slot, reachedLimit ? InventoryUtils.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.addAmount(reachedLimit ? limit : stack.getAmount());
			}
		}
		return reachedLimit ? InventoryUtils.copyStackWithSize(stack, stack.getAmount() - limit) : ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}
		validateSlotIndex(slot);
		ItemStack existing = getStackInSlot(slot);

		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int toExtract = Math.min(amount, existing.getMaxAmount());
		if (existing.getAmount() <= toExtract) {
			if (!simulate) {
				setStackInSlot(slot, ItemStack.EMPTY);
			}
			return existing;
		} else {
			if (!simulate) {
				setStackInSlot(slot, InventoryUtils.copyStackWithSize(existing, existing.getAmount() - toExtract));
			}
			return InventoryUtils.copyStackWithSize(existing, toExtract);
		}
	}

	public int getSlotLimit(int slot) {
		return 64;
	}

	public void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= size) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + size + ")");
		}

	}

	public int getStackLimit(int slot,
	                         @Nonnull
		                         ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxAmount());
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return true;
	}

}
