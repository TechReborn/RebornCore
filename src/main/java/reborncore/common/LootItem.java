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


package reborncore.common;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

/**
 * Created by Gigabit101 on 16/08/2016.
 */
public class LootItem {
	ItemStack item;
	double chance;
	int minSize;
	int maxSize;
	ResourceLocation lootTableList;

	public LootItem(ItemStack item, double chance, ResourceLocation lootTableList) {
		this(item, chance, 1, 1, lootTableList);
	}

	public LootItem(ItemStack item, double chance, int minSize, int maxSize, ResourceLocation lootTableList) {
		this.item = item;
		this.chance = chance;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.lootTableList = lootTableList;
	}

	public ItemStack createStack(Random rnd) {
		int size = minSize;
		if (maxSize > minSize) {
			size += rnd.nextInt(maxSize - minSize + 1);
		}

		ItemStack result = item.copy();
		result.setCount(size);
		return result;
	}

	public ResourceLocation getLootTableList() {
		return this.lootTableList;
	}
}
