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

package reborncore.common.powerSystem.mixin;

import ic2.api.item.IElectricItem;
import net.minecraft.item.ItemStack;
import reborncore.api.power.EnumPowerTier;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.mixin.api.Inject;
import reborncore.mixin.api.Mixin;

@Mixin(target = "")
public class EUItemPowerTrait implements IElectricItem {

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return getInterface().canAcceptEnergy(itemStack);
	}

	@Inject
	@Override
	public double getMaxCharge(ItemStack itemStack) {
		return getInterface().getMaxPower(itemStack);
	}

	@Inject
	@Override
	public int getTier(ItemStack itemStack) {
		return EnumPowerTier.getTeir((int) getInterface().getMaxTransfer(itemStack)).getIC2Tier();
	}

	@Inject
	@Override
	public double getTransferLimit(ItemStack itemStack) {
		return getInterface().getMaxTransfer(itemStack);
	}

	@Inject
	public IEnergyInterfaceItem getInterface() {
		return (IEnergyInterfaceItem) this;
	}
}
