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

package reborncore.common.powerSystem.forge;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.common.powerSystem.TilePowerAcceptor;

import javax.annotation.Nullable;

public class ForgeEnergyStorage implements IEnergyStorage {

	TilePowerAcceptor acceptor;
	@Nullable
	EnumFacing facing;

	public ForgeEnergyStorage(TilePowerAcceptor acceptor,
	                          @Nullable
		                          EnumFacing facing) {
		this.acceptor = acceptor;
		this.facing = facing;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return acceptor.receiveEnergy(facing, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return acceptor.extractEnergy(facing, maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return acceptor.getEnergyStored(facing);
	}

	@Override
	public int getMaxEnergyStored() {
		return acceptor.getMaxEnergyStored(facing);
	}

	@Override
	public boolean canExtract() {
		return acceptor.canProvideEnergy(facing);
	}

	@Override
	public boolean canReceive() {
		return acceptor.canAcceptEnergy(facing);
	}

	public void setFacing(
		@Nullable
			EnumFacing facing) {
		this.facing = facing;
	}
}
