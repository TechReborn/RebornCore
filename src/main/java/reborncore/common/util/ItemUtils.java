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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 12/04/15.
 */
public class ItemUtils {

	public static boolean isItemEqual(final ItemStack a, final ItemStack b, final boolean matchDamage,
	                                  final boolean matchNBT) {
		if (a.isEmpty() || b.isEmpty())
			return false;
		if (a.getItem() != b.getItem())
			return false;
		if (matchNBT && !ItemStack.areItemStackTagsEqual(a, b))
			return false;
		if (matchDamage && a.getHasSubtypes()) {
			if (isWildcard(a) || isWildcard(b))
				return true;
			if (a.getItemDamage() != b.getItemDamage())
				return false;
		}
		return true;
	}

	public static boolean isItemEqual(ItemStack a, ItemStack b, boolean matchDamage, boolean matchNBT,
	                                  boolean useOreDic) {
		if (isItemEqual(a, b, matchDamage, matchNBT)) {
			return true;
		}
		if (a.isEmpty() || b.isEmpty())
			return false;
		if (useOreDic) {
			for (int inta : OreDictionary.getOreIDs(a)) {
				for (int intb : OreDictionary.getOreIDs(b)) {
					if (inta == intb) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isInputEqual(Object input, ItemStack other, boolean matchDamage, boolean matchNBT,
	                                   boolean useOreDic) {
		if (input instanceof ItemStack) {
			return isItemEqual((ItemStack) input, other, matchDamage, matchNBT, useOreDic);
		} else if (input instanceof String) {
			NonNullList<ItemStack> ores = OreDictionary.getOres((String) input);
			for (ItemStack stack : ores) {
				if (isItemEqual(stack, other, matchDamage, matchNBT, false)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isWildcard(ItemStack stack) {
		return isWildcard(stack.getItemDamage());
	}

	public static boolean isWildcard(int damage) {
		return damage == -1 || damage == OreDictionary.WILDCARD_VALUE;
	}

	public static void writeItemToNBT(ItemStack stack, NBTTagCompound data) {
		if (stack.isEmpty() || stack.getCount() <= 0)
			return;
		if (stack.getCount() > 127)
			stack.setCount(127);
		stack.writeToNBT(data);
	}

	public static ItemStack readItemFromNBT(NBTTagCompound data) {
		return new ItemStack(data);
	}

	public static List<ItemStack> getStackWithAllOre(ItemStack stack) {
		if (stack.isEmpty()) {
			return new ArrayList<ItemStack>();
		}
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (int oreID : OreDictionary.getOreIDs(stack)) {
			for (ItemStack ore : OreDictionary.getOres(OreDictionary.getOreName(oreID))) {
				ItemStack newOre = ore.copy();
				newOre.setCount(stack.getCount());
				list.add(newOre);
			}
		}
		if (list.isEmpty()) {
			list.add(stack);
		}
		return list;
	}
	
	public static double getPowerForDurabilityBar(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0.0;
		}
		IEnergyStorage capEnergy = stack.getCapability(CapabilityEnergy.ENERGY, null);
		double energy = (double) capEnergy.getEnergyStored();
		double maxEnergy = (double) capEnergy.getMaxEnergyStored();
		return energy /  maxEnergy;
	}

	public static boolean isActive(ItemStack stack) {
		if (!stack.isEmpty() && stack.getTagCompound() != null && stack.getTagCompound().getBoolean("isActive")) {
			return true;
		}
		return false;
	}
}
