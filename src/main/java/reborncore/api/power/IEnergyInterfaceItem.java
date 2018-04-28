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

package reborncore.api.power;

import net.minecraft.item.ItemStack;

@Deprecated //TODO remove in 1.13 and use forge energy, the implimentation is broken in a few places, and its just a pain to maintain.
public interface IEnergyInterfaceItem extends IEnergyItemInfo {
	/**
	 * @param stack The {@link ItemStack} that contains the power
	 * @return Amount of energy in the tile
	 */
	public double getEnergy(ItemStack stack);

	/**
	 * Sets the energy in the tile
	 *
	 * @param energy the amount of energy to set.
	 * @param stack The {@link ItemStack} that contains the power
	 */
	public void setEnergy(double energy, ItemStack stack);

	/**
	 * @param energy amount of energy to add to the tile
	 * @param stack The {@link ItemStack} that contains the power
	 * @return will return true if can fit all
	 */
	public boolean canAddEnergy(double energy, ItemStack stack);

	/**
	 * Will try add add the full amount of energy.
	 *
	 * @param energy amount to add
	 * @param stack The {@link ItemStack} that contains the power
	 * @return The amount of energy that was added.
	 */
	public double addEnergy(double energy, ItemStack stack);

	/**
	 * Will try add add the full amount of energy, if simulate is true it wont
	 * add the energy
	 *
	 * @param energy amount to add
	 * @param stack The {@link ItemStack} that contains the power
	 * @return The amount of energy that was added.
	 */
	public double addEnergy(double energy, boolean simulate, ItemStack stack);

	/**
	 * Returns true if it can use the full amount of energy
	 *
	 * @param energy amount of energy to use from the tile.
	 * @param stack The {@link ItemStack} that contains the power
	 * @return if all the energy can be used.
	 */
	public boolean canUseEnergy(double energy, ItemStack stack);

	/**
	 * Will try and use the full amount of energy
	 *
	 * @param energy energy to use
	 * @param stack The {@link ItemStack} that contains the power
	 * @return the amount of energy used
	 */
	public double useEnergy(double energy, ItemStack stack);

	/**
	 * Will try and use the full amount of energy, if simulate is true it wont
	 * add the energy
	 *
	 * @param energy energy to use
	 * @param simulate if true it will not use the item, it will only simulate it.
	 * @param stack The {@link ItemStack} that contains the power
	 * @return the amount of energy used
	 */
	public double useEnergy(double energy, boolean simulate, ItemStack stack);

}
