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

import io.github.prospector.silk.fluid.FluidInstance;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import reborncore.common.blockentity.FluidConfiguration;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import net.minecraft.fluid.Fluid;

import javax.annotation.Nullable;

public class Tank  {

	private final String name;

	private int amount;
	private Fluid fluid;



	@Nullable
	private Direction side = null;
	MachineBaseBlockEntity machine;

	public Tank(String name, int capacity, MachineBaseBlockEntity blockEntity) {
		super();
		this.name = name;
		//this.blockEntity = blockEntity;
		this.machine = blockEntity;
	}

	public FluidInstance getFluid(){
		return new FluidInstance();
	}

	public int getCapacity(){
		return 1;
	}

	public boolean isEmpty() {
		return getFluid() == null || getFluid().getAmount() <= 0;
	}

	public boolean isFull() {
		return getFluid() != null && getFluid().getAmount() >= getCapacity();
	}

	public Fluid getFluidType() {
		return getFluid() != null ? getFluid().getFluid() : null;
	}

	public final CompoundTag write(CompoundTag nbt) {
		CompoundTag tankData = new CompoundTag();
		nbt.put(name, tankData);
		return nbt;
	}

	public void setFluidAmount(int amount) {
		if (fluid != null) {
			this.amount = amount;
		}
	}

	public final Tank read(CompoundTag nbt) {
		if (nbt.containsKey(name)) {
			// allow to read empty tanks
			setFluid(null);

			CompoundTag tankData = nbt.getCompound(name);
		}
		return this;
	}

	public void setFluid(Fluid o) {

	}

	//@Override
	public Tank readFromNBT(CompoundTag nbt) {
		return read(nbt);
	}

	//@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		return write(nbt);
	}

	@Nullable
	public Direction getSide() {
		return side;
	}

	public void setSide(
		@Nullable
			Direction side) {
		this.side = side;
	}

	//@Override
	public boolean canFill() {
		if (side != null) {
			if (machine.fluidConfiguration != null) {
				FluidConfiguration.FluidConfig fluidConfig = machine.fluidConfiguration.getSideDetail(side);
				if (fluidConfig == null) {
					return true;
				}
				return fluidConfig.getIoConfig().isInsert();
			}
		}
		return true;
	}

	//@Override
	public boolean canDrain() {
		if (side != null) {
			if (machine.fluidConfiguration != null) {
				FluidConfiguration.FluidConfig fluidConfig = machine.fluidConfiguration.getSideDetail(side);
				if (fluidConfig == null) {
					return true;
				}
				return fluidConfig.getIoConfig().isExtact();
			}
		}
		return true;
	}

	//TODO optimise
	public void compareAndUpdate() {
//		if (blockEntity == null || blockEntity.getWorld().isRemote) {
//			return;
//		}
//		FluidStack current = this.getFluid();
//		if (current != null) {
//			if (lastBeforeUpdate != null) {
//				if (Math.abs(current.amount - lastBeforeUpdate.amount) >= 500) {
//					NetworkManager.sendToTracking(ClientBoundPackets.createCustomDescriptionPacket(blockEntity), blockEntity.getWorld(), blockEntity.getPos());
//					lastBeforeUpdate = current.copy();
//				} else if (lastBeforeUpdate.amount < this.getCapacity() && current.amount == this.getCapacity() || lastBeforeUpdate.amount == this.getCapacity() && current.amount < this.getCapacity()) {
//					NetworkManager.sendToTracking(ClientBoundPackets.createCustomDescriptionPacket(blockEntity), blockEntity.getWorld(), blockEntity.getPos());
//					lastBeforeUpdate = current.copy();
//				}
//			} else {
//				NetworkManager.sendToTracking(ClientBoundPackets.createCustomDescriptionPacket(blockEntity), blockEntity.getWorld(), blockEntity.getPos());
//				lastBeforeUpdate = current.copy();
//			}
//		} else if (lastBeforeUpdate != null) {
//			NetworkManager.sendToTracking(ClientBoundPackets.createCustomDescriptionPacket(blockEntity), blockEntity.getWorld(), blockEntity.getPos());
//			lastBeforeUpdate = null;
//		}
	}

	public int getFluidAmount() {
		return 0;
	}

	public void drain(int currentWithdraw, boolean b) {

	}

	public int fill(FluidInstance stack, boolean bool){
		return 0;
	}


	public void setBlockEntity(BlockEntity blockEntityBaseFluidGenerator) {

	}
}
