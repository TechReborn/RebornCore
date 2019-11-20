/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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
import reborncore.api.power.IEnergyInterfaceItem;

@SuppressWarnings("deprecation")
public abstract class PoweredItem {

	public static boolean canUseEnergy(double energy, ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).canUseEnergy(energy, stack);
		} else {
			return false;
		}
	}

	public static double useEnergy(double energy, ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).useEnergy(energy, stack);
		} else {
			return 0;
		}
	}

	public static void setEnergy(double energy, ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			((IEnergyInterfaceItem) stack.getItem()).setEnergy(energy, stack);
		}
	}

	public static double getEnergy(ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).getEnergy(stack);
		} else {
			return 0;
		}
	}

	public static double addEnergy(double energy, ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).addEnergy(energy, stack);
		}
		return 0;
	}

	public static double getMaxPower(ItemStack stack) {
		if (stack.getItem() instanceof IEnergyInterfaceItem) {
			return ((IEnergyInterfaceItem) stack.getItem()).getMaxPower(stack);
		}
		return 0;
	}

}
