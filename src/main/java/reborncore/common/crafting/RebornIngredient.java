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

package reborncore.common.crafting;

import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.util.function.Predicate;

//Mainly a stack size aware wrapper for Ingredient
public class RebornIngredient implements Predicate<ItemStack> {

	public static final int NO_SIZE = -1;

	private final Ingredient base;

	private final int size;

	private RebornIngredient(Ingredient base, int size) {
		this.base = base;
		this.size = size;
	}

	private RebornIngredient(Ingredient base) {
		this(base, NO_SIZE);
	}

	@Override
	public boolean test(ItemStack itemStack) {
		return base.test(itemStack) && (size == NO_SIZE || itemStack.getCount() >= size);
	}

	public Ingredient getBase() {
		return base;
	}

	public int getSize() {
		if(size == NO_SIZE){
			return 1;
		}
		return size;
	}

	public JsonElement serialize(){
		JsonElement json = base.toJson();
		if(json.isJsonObject()){
			json.getAsJsonObject().addProperty("size", size);
		}
		return json;
	}

	public static RebornIngredient deserialize(@Nullable JsonElement json) {
		Validate.notNull(json, "item cannot be null");
		Ingredient base = Ingredient.fromJson(json);
		int size = NO_SIZE;
		if(json.isJsonObject()){
			if(json.getAsJsonObject().has("size")){
				size = JsonHelper.getInt(json.getAsJsonObject(), "size");
			}
		} else if (json.isJsonArray()){
			//TODO not really supported? might be best to ensure all sizes are the same? or find a nice way to allow mutli sizes, could be possible if required
		}
		return new RebornIngredient(base, size);
	}

}
