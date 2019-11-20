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

import net.minecraft.util.EnumFacing;

public interface IEnergyInterfaceTile {

	/**
	 * @return Amount of energy in the tile
	 */
	public double getEnergy();

	/**
	 * Sets the energy in the tile
	 *
	 * @param energy the amount of energy to set.
	 */
	public void setEnergy(double energy);

	/**
	 * Gets the max stored energy in the tile
	 *
	 * @return The max energy
	 */
	public double getMaxPower();

	/**
	 * @param energy amount of energy to add to the tile
	 * @return will return true if can fit all
	 */
	public boolean canAddEnergy(double energy);

	/**
	 * Will try add add the full amount of energy.
	 *
	 * @param energy amount to add
	 * @return The amount of energy that was added.
	 */
	public double addEnergy(double energy);

	/**
	 * Will try add add the full amount of energy, if simulate is true it wont
	 * add the energy
	 *
	 * @param energy amount to add
	 * @param simulate set to true to simulate not perform the action.
	 * @return The amount of energy that was added.
	 */
	public double addEnergy(double energy, boolean simulate);

	/**
	 * Returns true if it can use the full amount of energy
	 *
	 * @param energy amount of energy to use from the tile.
	 * @return if all the energy can be used.
	 */
	public boolean canUseEnergy(double energy);

	/**
	 * Will try and use the full amount of energy
	 *
	 * @param energy energy to use
	 * @return the amount of energy used
	 */
	public double useEnergy(double energy);

	/**
	 * Will try and use the full amount of energy, if simulate is true it wont
	 * add the energy
	 *
	 * @param energy energy to use
	 * @param simulate set to true to simulate not perform the action.
	 * @return the amount of energy used
	 */
	public double useEnergy(double energy, boolean simulate);

	/**
	 * @param direction The direction to insert energy into
	 * @return if the tile can accept energy from the direction
	 */
	public boolean canAcceptEnergy(EnumFacing direction);

	/**
	 * @param direction The direction to provide energy from
	 * @return true if the tile can provide energy to that direction
	 */
	public boolean canProvideEnergy(EnumFacing direction);

	/**
	 * Gets the max output, set to -1 if you don't want the tile to provide
	 * energy
	 *
	 * @return the max amount of energy outputted per tick.
	 */
	public double getMaxOutput();

	/**
	 * Return -1 if you don't want to accept power ever.
	 *
	 * @return The max amount of energy that can be added to the tile in one
	 * tick.
	 */
	public double getMaxInput();

	/**
	 * Gets the teir, used for machine explosions
	 *
	 * @return the teir
	 */
	public EnumPowerTier getTier();

}
