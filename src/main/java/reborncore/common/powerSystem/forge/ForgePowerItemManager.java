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
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PoweredItem;

public class ForgePowerItemManager implements IEnergyStorage {

	ItemStack stack;
	IEnergyItemInfo itemPowerInfo;

	public ForgePowerItemManager(ItemStack stack) {
		this.stack = stack;
		if (stack.getItem() instanceof IEnergyItemInfo) {
			itemPowerInfo = (IEnergyItemInfo) stack.getItem();
		}
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return receiveEnergy(stack, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return extractEnergy(stack, maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return getEnergyStored(stack);
	}

	@Override
	public int getMaxEnergyStored() {
		return getMaxEnergyStored(stack);
	}

	@Override
	public boolean canExtract() {
		return itemPowerInfo.canProvideEnergy(stack);
	}

	@Override
	public boolean canReceive() {
		return itemPowerInfo.canAcceptEnergy(stack);
	}

	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
		if (!RebornCoreConfig.getRebornPower().forge())
			return 0;
		if (!itemPowerInfo.canAcceptEnergy(container)) {
			return 0;
		}
		maxReceive *= RebornCoreConfig.euPerFU;
		int energyReceived = Math.min(getMaxEnergyStored(container) - getEnergyStored(container),
			Math.min((int) itemPowerInfo.getMaxTransfer(container) * RebornCoreConfig.euPerFU, maxReceive));

		if (!simulate) {
			PoweredItem.setEnergy(PoweredItem.getEnergy(container) + energyReceived, container);
		}
		return energyReceived / RebornCoreConfig.euPerFU;
	}

	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
		if (!RebornCoreConfig.getRebornPower().forge())
			return 0;
		if (!itemPowerInfo.canAcceptEnergy(container)) {
			return 0;
		}
		maxExtract *= RebornCoreConfig.euPerFU;
		int energyExtracted = Math.min(getEnergyStored(container), Math.min(maxExtract, maxExtract));

		if (!simulate) {
			PoweredItem.setEnergy(PoweredItem.getEnergy(container) - energyExtracted, container);
		}
		return energyExtracted / RebornCoreConfig.euPerFU;
	}

	public int getEnergyStored(ItemStack container) {
		if (!RebornCoreConfig.getRebornPower().forge())
			return 0;
		return ((int) PoweredItem.getEnergy(container) / RebornCoreConfig.euPerFU);
	}

	public int getMaxEnergyStored(ItemStack container) {
		if (!RebornCoreConfig.getRebornPower().forge())
			return 0;
		return ((int) itemPowerInfo.getMaxPower(container) / RebornCoreConfig.euPerFU);
	}
}
