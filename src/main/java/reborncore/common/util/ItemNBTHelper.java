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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemNBTHelper {

	/**
	 * Checks if an ItemStack has a Tag Compound
	 **/
	public static boolean detectNBT(ItemStack stack) {
		return stack.hasTagCompound();
	}

	/**
	 * Tries to initialize an NBT Tag Compound in an ItemStack, this will not do
	 * anything if the stack already has a tag compound
	 **/
	public static void initNBT(ItemStack stack) {
		if (!detectNBT(stack))
			injectNBT(stack, new NBTTagCompound());
	}

	/**
	 * Injects an NBT Tag Compound to an ItemStack, no checks are made
	 * previously
	 **/
	public static void injectNBT(ItemStack stack, NBTTagCompound nbt) {
		stack.setTagCompound(nbt);
	}

	/**
	 * Gets the NBTTagCompound in an ItemStack. Tries to init it previously in
	 * case there isn't one present
	 **/
	public static NBTTagCompound getNBT(ItemStack stack) {
		initNBT(stack);
		return stack.getTagCompound();
	}

	// SETTERS
	// ///////////////////////////////////////////////////////////////////

	public static void setBoolean(ItemStack stack, String tag, boolean b) {
		getNBT(stack).setBoolean(tag, b);
	}

	public static void setByte(ItemStack stack, String tag, byte b) {
		getNBT(stack).setByte(tag, b);
	}

	public static void setShort(ItemStack stack, String tag, short s) {
		getNBT(stack).setShort(tag, s);
	}

	public static void setInt(ItemStack stack, String tag, int i) {
		getNBT(stack).setInteger(tag, i);
	}

	public static void setLong(ItemStack stack, String tag, long l) {
		getNBT(stack).setLong(tag, l);
	}

	public static void setFloat(ItemStack stack, String tag, float f) {
		getNBT(stack).setFloat(tag, f);
	}

	public static void setDouble(ItemStack stack, String tag, double d) {
		getNBT(stack).setDouble(tag, d);
	}

	public static void setCompound(ItemStack stack, String tag, NBTTagCompound cmp) {
		if (!tag.equalsIgnoreCase("ench")) // not override the enchantments
			getNBT(stack).setTag(tag, cmp);
	}

	public static void setString(ItemStack stack, String tag, String s) {
		getNBT(stack).setString(tag, s);
	}

	public static void setList(ItemStack stack, String tag, NBTTagList list) {
		getNBT(stack).setTag(tag, list);
	}

	// GETTERS
	// ///////////////////////////////////////////////////////////////////

	public static boolean verifyExistance(ItemStack stack, String tag) {
		return stack != ItemStack.EMPTY && getNBT(stack).hasKey(tag);
	}

	public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
		return verifyExistance(stack, tag) ? getNBT(stack).getBoolean(tag) : defaultExpected;
	}

	public static byte getByte(ItemStack stack, String tag, byte defaultExpected) {
		return verifyExistance(stack, tag) ? getNBT(stack).getByte(tag) : defaultExpected;
	}

	public static short getShort(ItemStack stack, String tag, short defaultExpected) {
		return verifyExistance(stack, tag) ? getNBT(stack).getShort(tag) : defaultExpected;
	}

	public static int getInt(ItemStack stack, String tag, int defaultExpected) {
		return verifyExistance(stack, tag) ? getNBT(stack).getInteger(tag) : defaultExpected;
	}

	public static long getLong(ItemStack stack, String tag, long defaultExpected) {
		return verifyExistance(stack, tag) ? getNBT(stack).getLong(tag) : defaultExpected;
	}

	public static float getFloat(ItemStack stack, String tag, float defaultExpected) {
		return verifyExistance(stack, tag) ? getNBT(stack).getFloat(tag) : defaultExpected;
	}

	public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
		return verifyExistance(stack, tag) ? getNBT(stack).getDouble(tag) : defaultExpected;
	}

	/**
	 * If nullifyOnFail is true it'll return null if it doesn't find any
	 * compounds, otherwise it'll return a new one.
	 **/
	public static NBTTagCompound getCompound(ItemStack stack, String tag, boolean nullifyOnFail) {
		return verifyExistance(stack, tag) ? getNBT(stack).getCompoundTag(tag)
		                                   : nullifyOnFail ? null : new NBTTagCompound();
	}

	public static String getString(ItemStack stack, String tag, String defaultExpected) {
		return verifyExistance(stack, tag) ? getNBT(stack).getString(tag) : defaultExpected;
	}

	public static NBTTagList getList(ItemStack stack, String tag, int objtype, boolean nullifyOnFail) {
		return verifyExistance(stack, tag) ? getNBT(stack).getTagList(tag, objtype)
		                                   : nullifyOnFail ? null : new NBTTagList();
	}
}
