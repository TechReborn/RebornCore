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
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import reborncore.common.util.NonNullListCollector;
import reborncore.common.util.serialization.SerializationUtil;
import reborncore.mixin.extensions.RecipeManagerExtensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeUtils {

	public static <T extends RebornRecipe> List<T> getRecipes(World world, RebornRecipeType<?> type){
		RecipeManagerExtensions recipeManagerExtensions = (RecipeManagerExtensions) world.getRecipeManager();
		//noinspection unchecked
		return new ArrayList<>(recipeManagerExtensions.getAll(type).values());
	}

	public static DefaultedList<ItemStack> deserializeItems(JsonElement jsonObject){
		if(jsonObject.isJsonArray()){
			return SerializationUtil.stream(jsonObject.getAsJsonArray()).map(entry -> deserializeItem(entry.getAsJsonObject())).collect(NonNullListCollector.toList());
		} else {
			return DefaultedList.create(deserializeItem(jsonObject.getAsJsonObject()));
		}
	}

	private static ItemStack deserializeItem(JsonObject jsonObject){
		Identifier resourceLocation = new Identifier(JsonHelper.getString(jsonObject, "item"));
		Item item = Registry.ITEM.get(resourceLocation);
		if(item == null){
			throw new IllegalStateException(resourceLocation + " did not exist");
		}
		int count = 1;
		if(jsonObject.has("count")){
			count = JsonHelper.getInt(jsonObject, "count");
		}
		//TODO support nbt
		return new ItemStack(item, count);
	}

}
