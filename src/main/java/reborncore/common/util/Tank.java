/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.CustomDescriptionPacket;

public class Tank extends FluidTank {

	private final String name;

	private FluidStack lastBeforeUpdate = null;

	Fluid lastFluid;
	int lastAmmount;

	public Tank(String name, int capacity, TileEntity tile) {
		super(capacity);
		this.name = name;
		this.tile = tile;
	}

	public boolean isEmpty() {
		return getFluid() == null || getFluid().amount <= 0;
	}

	public boolean isFull() {
		return getFluid() != null && getFluid().amount >= getCapacity();
	}

	public Fluid getFluidType() {
		return getFluid() != null ? getFluid().getFluid() : null;
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound tankData = new NBTTagCompound();
		super.writeToNBT(tankData);
		nbt.setTag(name, tankData);
		return nbt;
	}

	public void setFluidAmount(int amount) {
		if (fluid != null) {
			fluid.amount = amount;
		}
	}

	@Override
	public final FluidTank readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey(name)) {
			// allow to read empty tanks
			setFluid(null);

			NBTTagCompound tankData = nbt.getCompoundTag(name);
			super.readFromNBT(tankData);
		}
		return this;
	}

	//TODO optimise
	public void compareAndUpdate() {
		if (tile == null || tile.getWorld().isRemote) {
			return;
		}
		FluidStack current = this.getFluid();
		if (current != null) {
			if (lastBeforeUpdate != null) {
				if (Math.abs(current.amount - lastBeforeUpdate.amount) >= 500) {
					NetworkManager.sendToWorld(new CustomDescriptionPacket(tile), tile.getWorld());
					lastBeforeUpdate = current.copy();
				} else if (lastBeforeUpdate.amount < this.getCapacity() && current.amount == this.getCapacity() || lastBeforeUpdate.amount == this.getCapacity() && current.amount < this.getCapacity()) {
					NetworkManager.sendToWorld(new CustomDescriptionPacket(tile), tile.getWorld());
					lastBeforeUpdate = current.copy();
				}
			} else {
				NetworkManager.sendToWorld(new CustomDescriptionPacket(tile), tile.getWorld());
				lastBeforeUpdate = current.copy();
			}
		} else if (lastBeforeUpdate != null) {
			NetworkManager.sendToWorld(new CustomDescriptionPacket(tile), tile.getWorld());
			lastBeforeUpdate = null;
		}
	}

}
