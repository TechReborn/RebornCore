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


package reborncore.api.tile;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public interface IUpgradeable {
	/**
	 * Check if a tile can be upgrade.
	 *
	 * @return Wether the tile can be upgraded or not
	 */
	public default boolean canBeUpgraded() {
		return true;
	}

	/**
	 * Get the inventory slots for upgrades.
	 *
	 * @return The upgrade slots or null if there are none
	 */
	public ISidedInventory getUpgradeInventory();

	@Deprecated
	public default IInventory getUpgradeInvetory() {
		return getUpgradeInventory();
	}

	/**
	 * Get the number of upgrade slots (default = 4).
	 *
	 * @return The number of upgrade slots
	 */
	public default int getUpgradeSlotCount() {
		return 4;
	}

	/**
	 * Check if a certain upgrade is valid for this IUpgradable instance.
	 *
	 * @param upgrade The IUpgrade item
	 * @param stack The stack container the IUpgrade item
	 * @return True if the upgrade is valid or false otherwise
	 */
	public default boolean isUpgradeValid(IUpgrade upgrade, ItemStack stack) {
		return true;
	}
}
