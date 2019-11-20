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


package reborncore.api.power;

import net.minecraft.item.ItemStack;

public interface IEnergyItemInfo {

	/**
	 * Gets the max stored energy in the item
	 *
	 * @param stack The {@link ItemStack} that contains the power
	 * @return The max energy
	 */
	double getMaxPower(ItemStack stack);

	/**
	 * Can the item accept energy.
	 *
	 * @param stack The {@link ItemStack} that contains the power
	 * @return if it can accept energy
	 */
	boolean canAcceptEnergy(ItemStack stack);

	/**
	 * Can the item recieve energy
	 *
	 * @param stack The {@link ItemStack} that contains the power
	 * @return if it can provide energy
	 */
	boolean canProvideEnergy(ItemStack stack);

	/**
	 * @param stack The {@link ItemStack} that contains the power
	 * @return Max amount of energy that can be transfered in one tick.
	 */
	double getMaxTransfer(ItemStack stack);
}
