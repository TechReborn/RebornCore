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


package powercrystals.minefactoryreloaded.api;

import net.minecraft.item.ItemStack;

public interface IDeepStorageUnit {

	/**
	 * @return A populated ItemStack with stackSize for the full amount of
	 * materials in the DSU. <br>
	 * May have a stackSize > getMaxStackSize(). May have a stackSize of
	 * 0 (indicating locked contents).
	 */
	ItemStack getStoredItemType();

	/**
	 * Sets the total amount of the item currently being stored, or zero if all
	 * items are to be removed.
	 */
	void setStoredItemCount(int amount);

	/**
	 * Sets the type of the stored item and initializes the number of stored
	 * items to amount.
	 * <p>
	 * Will overwrite any existing stored items.
	 */
	void setStoredItemType(ItemStack type, int amount);

	/**
	 * @return The maximum number of items the DSU can hold. <br>
	 * May change based on the current type stored.
	 */
	int getMaxStoredCount();

}
