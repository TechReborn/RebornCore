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

package reborncore.modcl;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prospector
 */
public class ItemMetadataCL extends ItemCL {
	public List<String> types = new ArrayList<>();

	public ItemMetadataCL(ModCL mod, String name, String blockstateLocation) {
		super(mod, name, false);
		setHasSubtypes(true);
		mod.customBlockStates.put(this, blockstateLocation);
	}

	public ItemMetadataCL(ModCL mod, String name) {
		this(mod, name, "");
	}

	public ItemStack getStack(String name) {
		return getStack(name, 1);
	}

	public ItemStack getStack(String name, int count) {
		for (String type : types) {
			if (type.equalsIgnoreCase(name)) {
				ItemStack stack = new ItemStack(mod.getRegistry().itemRegistry.get(this.name), count, types.indexOf(name));
				stack.setCount(count);
				return stack;
			}
		}
		throw new InvalidParameterException("Stack not found: " + name);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (int meta = 0; meta < types.size(); meta++) {
			list.add(new ItemStack(this, 1, meta));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int meta = itemStack.getItemDamage();
		if (meta < 0 || meta >= types.size()) {
			meta = 0;
		}
		return super.getUnlocalizedName() + "." + types.get(meta);
	}
}
