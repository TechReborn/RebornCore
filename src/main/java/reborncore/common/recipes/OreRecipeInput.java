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


package reborncore.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OreRecipeInput implements IRecipeInput {

	String oreDictName;
	int size = 1;

	public OreRecipeInput(String oreDictName, int size) {
		this.oreDictName = oreDictName;
		this.size = size;
		Validate.notEmpty(getAllStacks());
	}

	public OreRecipeInput(String oreDictName) {
		this.oreDictName = oreDictName;
		Validate.notEmpty(getAllStacks());
	}

	@Override
	public ItemStack getItemStack() {
		List<ItemStack> list = getAllStacks();
		if (list.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return list.get(0);
	}

	@Override
	public List<ItemStack> getAllStacks() {
		if (!OreDictionary.doesOreNameExist(oreDictName)) {
			return NonNullList.create();
		}
		NonNullList<ItemStack> list = OreDictionary.getOres(oreDictName);
		return list.stream()
			.filter(Objects::nonNull)
			.map(stack -> {
				ItemStack clone = stack.copy();
				clone.setCount(size);
				return clone;
			})
			.filter(stack -> !stack.isEmpty())
			.collect(Collectors.toList());
	}
}
