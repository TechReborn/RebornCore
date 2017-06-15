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

package reborncore.common.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import reborncore.RebornCore;

import java.util.ArrayList;

public class OreUtil {

	public static ArrayList<String> oreNames = new ArrayList<String>();

	public static boolean doesOreExistAndValid(String name) {
		return !OreDictionary.getOres(name).isEmpty();
	}

	public static ItemStack getStackFromName(String name) {
		return getStackFromName(name, 1);
	}

	public static ItemStack getStackFromName(String name, int stackSize) {
		ItemStack stack = OreDictionary.getOres(name).get(0).copy();
		stack.setCount(stackSize);
		return stack;
	}

	public static void scanForOres() {
		String[] validPrefixes = new String[] { "ingot", "ore", "gem", "crushed", "plate", "nugget", "dustSmall", "dustTiny", "dust",
			"block" };
		for (String oreDicName : OreDictionary.getOreNames()) {
			for (String prefix : validPrefixes) {
				if (oreDicName.startsWith(prefix)) {
					if (!oreNames.contains(oreDicName.replace(prefix, "").toLowerCase())) {
						oreNames.add(oreDicName.replace(prefix, "").toLowerCase());
					}
				}
			}
		}
		RebornCore.logHelper.info("Found " + oreNames.size() + " ores");
	}

	public static boolean hasIngot(String name) {
		return doesOreExistAndValid("ingot" + capitalizeFirstLetter(name));
	}

	public static boolean hasOre(String name) {
		return doesOreExistAndValid("ore" + capitalizeFirstLetter(name));
	}

	public static boolean hasCrushedOre(String name) {
		return doesOreExistAndValid("crushed" + capitalizeFirstLetter(name));
	}

	public static boolean hasPlate(String name) {
		return doesOreExistAndValid("plate" + capitalizeFirstLetter(name));
	}

	public static boolean hasNugget(String name) {
		return doesOreExistAndValid("nugget" + capitalizeFirstLetter(name));
	}

	public static boolean hasDustSmall(String name) {
		return doesOreExistAndValid("dustSmall" + capitalizeFirstLetter(name));
	}

	public static boolean hasDust(String name) {
		return doesOreExistAndValid("dust" + capitalizeFirstLetter(name));
	}

	public static boolean hasBlock(String name) {
		return doesOreExistAndValid("block" + capitalizeFirstLetter(name));
	}

	public static String capitalizeFirstLetter(String original) {
		if (original.length() == 0)
			return original;
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

}
