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

package reborncore.api.praescriptum.ingredients.input;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * @author estebes
 */
public class OreDictionaryInputIngredient extends InputIngredient<String> {
	public static OreDictionaryInputIngredient of(String ingredient) {
		return of(ingredient, 1);
	}

	public static OreDictionaryInputIngredient of(String ingredient, int amount) {
		return of(ingredient, amount, null);
	}

	public static OreDictionaryInputIngredient of(String ingredient, int amount, Integer meta) {
		return new OreDictionaryInputIngredient(ingredient, amount, meta);
	}

	protected OreDictionaryInputIngredient(String ingredient) {
		this(ingredient, 1);
	}

	protected OreDictionaryInputIngredient(String ingredient, int amount) {
		this(ingredient, amount, null);
	}

	protected OreDictionaryInputIngredient(String ingredient, int amount, Integer meta) {
		super(ingredient);

		this.amount = amount;
		this.meta = meta;
	}

	@Override
	public Object getUnspecific() {
		return null;
	}

	@Override
	public InputIngredient<String> copy() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public boolean isEmpty() {
		return amount <= 0;
	}

	@Override
	public int getCount() {
		return amount;
	}

	@Override
	public void shrink(int amount) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public boolean matches(Object other) {
		if (!(other instanceof ItemStack)) return false;

		List<ItemStack> inputs = getEquivalents();
		boolean useOreStackMeta = meta == null;
		Item subjectItem = ((ItemStack) other).getItem();
		int subjectMeta = ((ItemStack) other).getItemDamage();

		return inputs.stream()
			.anyMatch(entry -> {
				Item oreItem = entry.getItem();

				int metaRequired = useOreStackMeta ? entry.getItemDamage() : meta;

				return subjectItem == oreItem && (subjectMeta == metaRequired ||
					metaRequired == OreDictionary.WILDCARD_VALUE);
			});
	}

	@Override
	public boolean matchesStrict(Object other) {
		return other instanceof String && ingredient.equals(other);
	}

	@Override
	public String toFormattedString() {
		return ingredient;
	}

	private List<ItemStack> getEquivalents() {
		if (equivalents != null) return equivalents;

		// cache the ore list by making use of the fact that forge always uses the same list,
		// unless it's EMPTY_LIST, which should never happen.
		List<ItemStack> ret = OreDictionary.getOres((String) ingredient);

		if (ret != OreDictionary.EMPTY_LIST) equivalents = ret;

		return ret;
	}

	// Fields >>
	public final int amount;
	public final Integer meta;

	private List<ItemStack> equivalents;
	// << Fields
}
