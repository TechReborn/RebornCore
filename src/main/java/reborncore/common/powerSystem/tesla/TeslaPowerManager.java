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

package reborncore.common.powerSystem.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.TilePowerAcceptor;

import java.util.List;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class TeslaPowerManager implements ITeslaPowerManager {

	AdvancedTeslaContainer container;

	@Override
	public void readFromNBT(NBTTagCompound compound, TilePowerAcceptor powerAcceptor) {
		this.container = new AdvancedTeslaContainer(compound.getTag("TeslaContainer"), powerAcceptor);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound, TilePowerAcceptor powerAcceptor) {
		compound.setTag("TeslaContainer", this.container.writeNBT());
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing, TilePowerAcceptor powerAcceptor) {
		if (capability == TeslaCapabilities.CAPABILITY_CONSUMER && powerAcceptor.canAcceptEnergy(facing)) {
			this.container.tile = powerAcceptor;
			return (T) this.container;
		} else if (capability == TeslaCapabilities.CAPABILITY_PRODUCER && powerAcceptor.canProvideEnergy(facing)) {
			this.container.tile = powerAcceptor;
			return (T) this.container;
		}
		if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
			this.container.tile = powerAcceptor;
			return (T) this.container;
		}
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing, TilePowerAcceptor powerAcceptor) {
		if (capability == TeslaCapabilities.CAPABILITY_CONSUMER && powerAcceptor.canAcceptEnergy(facing)) {
			return true;
		} else if (capability == TeslaCapabilities.CAPABILITY_PRODUCER && powerAcceptor.canProvideEnergy(facing)) {
			return true;
		}
		if (capability == TeslaCapabilities.CAPABILITY_HOLDER)
			return true;
		return false;
	}

	@Override
	public void update(TilePowerAcceptor powerAcceptor) {
		if (powerAcceptor.canProvideEnergy(null)) {
			List<ITeslaConsumer> connectedConsumers = TeslaUtils.getConnectedCapabilities(TeslaCapabilities.CAPABILITY_CONSUMER, powerAcceptor.getWorld(), powerAcceptor.getPos());

			for (ITeslaConsumer consumer : connectedConsumers) {
				double euToTransfer = Math.min(powerAcceptor.getEnergy(), powerAcceptor.getMaxOutput());
				long teslaTransferred = consumer.givePower((long) (euToTransfer / RebornCoreConfig.euPerFU), false);
				powerAcceptor.useEnergy(teslaTransferred * RebornCoreConfig.euPerFU);

				if (powerAcceptor.getEnergy() <= 0)
					break;
			}
		}
	}

	@Override
	public void created(TilePowerAcceptor powerAcceptor) {
		this.container = new AdvancedTeslaContainer(powerAcceptor);
	}

	@Override
	public String getDisplayableTeslaCount(long tesla) {
		return TeslaUtils.getDisplayableTeslaCount(tesla * RebornCoreConfig.euPerFU);
	}

	@Override
	public boolean isTeslaCapability(Capability<?> capability) {
		return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_PRODUCER || capability == TeslaCapabilities.CAPABILITY_HOLDER;
	}

	public static ITeslaPowerManager getPowerManager() {
		return new TeslaPowerManager();
	}
}
