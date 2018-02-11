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

package reborncore.common.powerSystem.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.TilePowerAcceptor;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class AdvancedTeslaContainer implements ITeslaConsumer, ITeslaHolder, ITeslaProducer {

	public TilePowerAcceptor tile;

	public AdvancedTeslaContainer(TilePowerAcceptor tile) {
		this.tile = tile;
	}

	public AdvancedTeslaContainer(NBTBase nbt, TilePowerAcceptor tile) {
		this.tile = tile;
		this.readNBT(nbt);
	}

	public long getStoredPower() {
		return (long) tile.getEnergy() * RebornCoreConfig.euPerFU;
	}

	//Receive
	public long givePower(long tesla, boolean simulated) {
		double euToAdd = tesla * RebornCoreConfig.euPerFU;

		if (tile.canAddEnergy(euToAdd)) {
			double euAdded = tile.addEnergy(euToAdd);
			return (long) (euAdded / RebornCoreConfig.euPerFU);
		} else {
			return 0;
		}
	}

	//Take power out
	public long takePower(long tesla, boolean simulated) {
		double euToTake = tesla * RebornCoreConfig.euPerFU;

		if (tile.canUseEnergy(euToTake)) {
			double euTaken = tile.useEnergy(euToTake);
			return (long) (euTaken / RebornCoreConfig.euPerFU);
		} else {
			return 0;
		}
	}

	public long getCapacity() {
		return (long) tile.getMaxPower() * RebornCoreConfig.euPerFU;
	}

	public long getInputRate() {
		return (long) tile.getMaxInput() * RebornCoreConfig.euPerFU;
	}

	public long getOutputRate() {
		return (long) tile.getMaxOutput() * RebornCoreConfig.euPerFU;
	}

	public NBTBase writeNBT() {
		NBTTagCompound dataTag = new NBTTagCompound();
		return dataTag;
	}

	public void readNBT(NBTBase nbt) {
	}

	public boolean isInputSide() {
		return true;
	}

	public boolean isOutputSide() {
		return true;
	}

}
