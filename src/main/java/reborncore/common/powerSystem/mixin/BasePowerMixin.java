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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.PoweredItem;
import reborncore.common.powerSystem.PoweredItemContainerProvider;
import reborncore.mixin.api.Inject;
import reborncore.mixin.api.Mixin;

import javax.annotation.Nullable;

@Mixin(target = "")
public abstract class BasePowerMixin implements IEnergyInterfaceItem {

	@Inject
	@Override
	public double getEnergy(ItemStack stack) {
		NBTTagCompound tagCompound = getOrCreateNbtData(stack);
		if (tagCompound.hasKey("charge")) {
			return tagCompound.getDouble("charge");
		}
		return 0;
	}

	@Inject
	@Override
	public void setEnergy(double energy, ItemStack stack) {
		NBTTagCompound tagCompound = getOrCreateNbtData(stack);
		tagCompound.setDouble("charge", energy);

		if (this.getEnergy(stack) > getMaxPower(stack)) {
			this.setEnergy(getMaxPower(stack), stack);
		} else if (this.getEnergy(stack) < 0) {
			this.setEnergy(0, stack);
		}
	}

	@Inject
	@Override
	public double addEnergy(double energy, ItemStack stack) {
		return addEnergy(energy, false, stack);
	}

	@Inject
	@Override
	public double addEnergy(double energy, boolean simulate, ItemStack stack) {
		double energyReceived = Math.min(getMaxPower(stack) - energy, Math.min(this.getMaxPower(stack), energy));

		if (!simulate) {
			setEnergy(energy + energyReceived, stack);
		}
		return energyReceived;
	}

	@Inject
	@Override
	public boolean canUseEnergy(double input, ItemStack stack) {
		return input <= getEnergy(stack);
	}

	@Inject
	@Override
	public double useEnergy(double energy, ItemStack stack) {
		return useEnergy(energy, false, stack);
	}

	@Inject
	@Override
	public double useEnergy(double extract, boolean simulate, ItemStack stack) {
		double energyExtracted = Math.min(extract, Math.min(this.getMaxTransfer(stack), extract));

		if (!simulate) {
			setEnergy(getEnergy(stack) - energyExtracted, stack);
		}
		return energyExtracted;
	}

	@Inject
	@Override
	public boolean canAddEnergy(double energy, ItemStack stack) {
		return this.getEnergy(stack) + energy <= getMaxPower(stack);
	}

	@Inject
	public NBTTagCompound getOrCreateNbtData(ItemStack itemStack) {
		NBTTagCompound tagCompound = itemStack.getTagCompound();
		if (tagCompound == null) {
			tagCompound = new NBTTagCompound();
			itemStack.setTagCompound(tagCompound);
		}

		return tagCompound;
	}

	@Inject
	public double getDurabilityForDisplay(ItemStack stack) {
		double charge = (PoweredItem.getEnergy(stack) / getMaxPower(stack));
		return 1 - charge;
	}

	@Inject
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Inject
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return PowerSystem.getDisplayPower().colour;
	}

	@Inject
	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack,
	                                            @Nullable
		                                            NBTTagCompound nbt) {
		return new PoweredItemContainerProvider(stack);
	}

}
