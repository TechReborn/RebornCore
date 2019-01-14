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

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.api.power.ExternalPowerHandler;
import reborncore.api.power.ExternalPowerManager;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.ExternalPowerSystems;
import reborncore.common.powerSystem.TilePowerAcceptor;
import reborncore.common.registration.RebornRegistry;

@RebornRegistry
public class ForgePowerManager implements ExternalPowerManager {
	@Override
	public ExternalPowerHandler createPowerHandler(TilePowerAcceptor acceptor) {
		if(!RebornCoreConfig.enableFE) {
			return null;
		}

		return new ForgePowerHandler(acceptor);
	}

	@Override
	public boolean isPoweredItem(ItemStack stack) {
		if(!RebornCoreConfig.enableFE) {
			return false;
		}

		return stack.hasCapability(CapabilityEnergy.ENERGY, null);
	}

	@Override
	public boolean isPoweredTile(TileEntity tileEntity, EnumFacing side) {
		if(!RebornCoreConfig.enableFE) {
			return false;
		}

		return tileEntity.hasCapability(CapabilityEnergy.ENERGY, side);
	}

	@Override
	public void dischargeItem(TilePowerAcceptor powerAcceptor, ItemStack stack) {
		if(!RebornCoreConfig.enableFE || isOtherPoweredItem(stack)) {
			return;
		}

		IEnergyStorage powerItem = stack.getCapability(CapabilityEnergy.ENERGY, null);
		if(powerItem == null) {
			return;
		}

		double chargeEnergy = Math.min(powerAcceptor.getFreeSpace(), powerAcceptor.getMaxInput()) * RebornCoreConfig.euPerFU;
		if (chargeEnergy <= 0.0) {
			return;
		}

		if (powerItem.getEnergyStored() > 0) {
			int extracted = powerItem.extractEnergy((int) chargeEnergy, false);
			powerAcceptor.addEnergy(extracted / RebornCoreConfig.euPerFU);
		}
	}

	@Override
	public void chargeItem(TilePowerAcceptor powerAcceptor, ItemStack stack) {
		if(!RebornCoreConfig.enableFE || isOtherPoweredItem(stack)) {
			return;
		}

		IEnergyStorage powerItem = stack.getCapability(CapabilityEnergy.ENERGY, null);
		if(powerItem == null) {
			return;
		}

		int maxReceive = powerItem.receiveEnergy((int)powerAcceptor.getMaxOutput() * RebornCoreConfig.euPerFU, true);

		double maxUse = Math.min((double) (maxReceive / RebornCoreConfig.euPerFU), powerAcceptor.getMaxOutput());

		if (powerAcceptor.getEnergy() >= 0.0 && maxReceive > 0) {
			powerItem.receiveEnergy((int) powerAcceptor.useEnergy(maxUse) * RebornCoreConfig.euPerFU, false);
		}
	}

	@Override
	public void chargeItem(ForgePowerItemManager powerAcceptor, ItemStack stack) {
		if(!RebornCoreConfig.enableFE || isOtherPoweredItem(stack)) {
			return;
		}

		IEnergyStorage powerItem = stack.getCapability(CapabilityEnergy.ENERGY, null);
		if(powerItem == null) {
			return;
		}

		int maxReceive = powerItem.receiveEnergy(powerAcceptor.extractEnergy(powerAcceptor.getEnergyStored(), true), true);

		if (maxReceive > 0) {
			powerItem.receiveEnergy(powerAcceptor.extractEnergy(maxReceive, false), false);
		}
	}

	/**
	 * Checks whether the provided tile is considered a powered tile by other power systems already
	 */
	private static boolean isOtherPoweredItem(ItemStack stack) {
		return ExternalPowerSystems.externalPowerHandlerList.stream()
				.filter(externalPowerManager -> !(externalPowerManager instanceof ForgePowerManager))
				.anyMatch(externalPowerManager -> externalPowerManager.isPoweredItem(stack));
	}
}
