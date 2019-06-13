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

package reborncore.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.recipes.IRecipeInput;

import java.util.List;

/**
 * Created by mark on 12/04/15.
 */
public class ItemUtils {

	public static boolean isItemEqual(final ItemStack a, final ItemStack b,
	                                  final boolean matchNBT) {
		if (a.isEmpty() || b.isEmpty()) {
			return false;
		}
		if (a.getItem() != b.getItem()) {
			return false;
		}
		if (matchNBT && !ItemStack.areTagsEqual(a, b)) {
			return false;
		}
		return true;
	}

	public static boolean isItemEqual(ItemStack a, ItemStack b, boolean matchNBT,
	                                  boolean useTags) {
		if (isItemEqual(a, b, matchNBT)) {
			return true;
		}
		if (a.isEmpty() || b.isEmpty()) {
			return false;
		}
		if (useTags) {
			//TODO tags
			throw new UnsupportedOperationException("1.13 tags");
		}
		return false;
	}

	//TODO tags
	public static boolean isInputEqual(Object input, ItemStack other, boolean matchNBT,
	                                   boolean useTags) {
		if (input instanceof ItemStack) {
			return isItemEqual((ItemStack) input, other, matchNBT, useTags);
		} else if (input instanceof String) {

		} else if (input instanceof IRecipeInput) {
			List<ItemStack> inputs = ((IRecipeInput) input).getAllStacks();
			for (ItemStack stack : inputs) {
				if (isItemEqual(stack, other, matchNBT, false)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void writeItemToNBT(ItemStack stack, CompoundTag data) {
		if (stack.isEmpty() || stack.getCount() <= 0) {
			return;
		}
		if (stack.getCount() > 127) {
			stack.setCount(127);
		}
		stack.toTag(data);
	}

	public static ItemStack readItemFromNBT(CompoundTag data) {
		return ItemStack.fromTag(data);
	}

	public static double getPowerForDurabilityBar(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0.0;
		}

		if (!(stack.getItem() instanceof IEnergyItemInfo)) {
			return 0.0;
		}

		throw new UnsupportedOperationException("Fix this");
	}

	public static boolean isActive(ItemStack stack) {
		if (!stack.isEmpty() && stack.getTag() != null && stack.getTag().getBoolean("isActive")) {
			return true;
		}
		return false;
	}
}
