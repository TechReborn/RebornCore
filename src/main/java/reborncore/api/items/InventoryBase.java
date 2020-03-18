/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.api.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.collection.DefaultedList;

public abstract class InventoryBase implements Inventory {

	private int size;
	private DefaultedList<ItemStack> stacks;

	public InventoryBase(int size) {
		this.size = size;
		stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	public Tag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		Inventories.toTag(tag, stacks);
		return tag;
	}

	public void deserializeNBT(CompoundTag tag) {
		stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
		Inventories.fromTag(tag, stacks);
	}

	@Override
	public int getInvSize() {
		return size;
	}

	@Override
	public boolean isInvEmpty() {
		return stacks.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getInvStack(int i) {
		return stacks.get(i);
	}

	@Override
	public ItemStack takeInvStack(int i, int i1) {
		ItemStack stack = Inventories.splitStack(stacks, i, i1);
		if (!stack.isEmpty()) {
			this.markDirty();
		}
		return stack;
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return Inventories.removeStack(stacks, i);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		stacks.set(i, itemStack);
		if (itemStack.getCount() > this.getInvMaxStackAmount()) {
			itemStack.setCount(this.getInvMaxStackAmount());
		}

		this.markDirty();
	}

	@Override
	public void markDirty() {
		//Stuff happens in the super methods
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public void clear() {
		stacks.clear();
	}

	public DefaultedList<ItemStack> getStacks() {
		return stacks;
	}
}
