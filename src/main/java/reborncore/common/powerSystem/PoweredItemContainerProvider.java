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

package reborncore.common.powerSystem;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by modmuss50 on 18/01/2017.
 */
public class PoweredItemContainerProvider implements ICapabilityProvider {

	ItemStack stack;
	final ForgePowerItemManager capEnergy;

	public PoweredItemContainerProvider(ItemStack stack) {
		this.stack = stack;
		this.capEnergy = new ForgePowerItemManager(stack);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		if (RebornCoreConfig.getRebornPower().forge() && capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (RebornCoreConfig.getRebornPower().forge() && capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(capEnergy);
		}
		return null;
	}
}
