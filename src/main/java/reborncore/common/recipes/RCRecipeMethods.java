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

package reborncore.common.recipes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Prospector
 */
public abstract class RCRecipeMethods {

	static ItemStack getStack(Item item) {
		return getStack(item, 1);
	}

	static ItemStack getStack(Item item, int count) {
		return getStack(item, count, 0);
	}

	static ItemStack getStack(Item item, boolean wildcard) {
		return getStack(item, 1, true);
	}

	static ItemStack getStack(Item item, int count, boolean wildcard) {
		return getStack(item, count, OreDictionary.WILDCARD_VALUE);
	}

	static ItemStack getStack(Item item, int count, int metadata) {
		return new ItemStack(item, count, metadata);
	}

	static ItemStack getStack(Block block) {
		return getStack(block, 1);
	}

	static ItemStack getStack(Block block, int count) {
		return getStack(block, count, 0);
	}

	static ItemStack getStack(Block block, boolean wildcard) {
		return getStack(block, 1, true);
	}

	static ItemStack getStack(Block block, int count, boolean wildcard) {
		return getStack(block, count, OreDictionary.WILDCARD_VALUE);
	}

	static ItemStack getStack(Block block, int count, int metadata) {
		return getStack(Item.getItemFromBlock(block), count, OreDictionary.WILDCARD_VALUE);
	}
}
