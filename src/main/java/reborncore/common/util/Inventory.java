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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class Inventory extends ItemStackHandler {

	private final String name;
	private final int stackLimit;
	private TileEntity tile;
	private boolean hasChanged = false;

	public Inventory(int size, String invName, int invStackLimit, TileEntity tileEntity) {
		super(size);
		name = invName;
		stackLimit = (invStackLimit == 64 ? Items.AIR.getItemStackLimit() : invStackLimit); //Blame asie for this
		this.tile = tileEntity;
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		super.setStackInSlot(slot, stack);
		setChanged();
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		ItemStack result = super.insertItem(slot, stack, simulate);
		setChanged();
		return result;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stack = super.extractItem(slot, amount, simulate);
		setChanged();
		return stack;
	}

	public ItemStack shrinkSlot(int slot, int count) {
		ItemStack stack = getStackInSlot(slot);
		stack.shrink(count);
		setChanged();
		return stack;
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : stacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void readFromNBT(NBTTagCompound data) {
		readFromNBT(data, "Items");
	}

	public void readFromNBT(NBTTagCompound data, String tag) {
		NBTTagCompound nbttaglist = data.getCompoundTag(tag);
		deserializeNBT(nbttaglist);
		hasChanged = true;
	}

	public void writeToNBT(NBTTagCompound data) {
		writeToNBT(data, "Items");
	}

	public void writeToNBT(NBTTagCompound data, String tag) {
		data.setTag(tag, serializeNBT());
	}

	public void setTile(TileEntity tileEntity) {
		tile = tileEntity;
	}

	public TileEntity getTileBase() {
		return tile;
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	public void setChanged() {
		this.hasChanged = true;
	}

	public void setChanged(boolean changed) {
		this.hasChanged = changed;
	}

	public void resetChanged() {
		this.hasChanged = false;
	}

	public int getStackLimit() {
		return stackLimit;
	}
}
