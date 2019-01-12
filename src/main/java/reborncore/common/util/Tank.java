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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.CustomDescriptionPacket;
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.TileMachineBase;

import javax.annotation.Nullable;

public class Tank extends FluidTank {

	private final String name;

	private FluidStack lastBeforeUpdate = null;

	Fluid lastFluid;
	int lastAmmount;

	@Nullable
	private EnumFacing side = null;
	TileMachineBase machine;

	public Tank(String name, int capacity, TileMachineBase tile) {
		super(capacity);
		this.name = name;
		this.tile = tile;
		this.machine = tile;
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

			NBTTagCompound tankData = nbt.getCompound(name);
			super.readFromNBT(tankData);
		}
		return this;
	}

	@Nullable
	public EnumFacing getSide() {
		return side;
	}

	public void setSide(
		@Nullable
			EnumFacing side) {
		this.side = side;
	}

	@Override
	public boolean canFill() {
		if (side != null) {
			if (machine.fluidConfiguration != null) {
				FluidConfiguration.FluidConfig fluidConfig = machine.fluidConfiguration.getSideDetail(side);
				if (fluidConfig == null) {
					return super.canFill();
				}
				return fluidConfig.getIoConfig().isInsert();
			}
		}
		return super.canFill();
	}

	@Override
	public boolean canDrain() {
		if (side != null) {
			if (machine.fluidConfiguration != null) {
				FluidConfiguration.FluidConfig fluidConfig = machine.fluidConfiguration.getSideDetail(side);
				if (fluidConfig == null) {
					return super.canDrain();
				}
				return fluidConfig.getIoConfig().isExtact();
			}
		}
		return super.canDrain();
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
