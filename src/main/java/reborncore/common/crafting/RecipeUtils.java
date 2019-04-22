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
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import reborncore.common.util.NonNullListCollector;
import reborncore.common.util.serialization.SerializationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeUtils {

	public static <R extends Recipe> List<R> getRecipes(World world, RecipeType<R> type){
		List<R> recipes = new ArrayList<>();
		for(IRecipe recipe : world.getRecipeManager().getRecipes()){
			if(recipe instanceof Recipe && ((Recipe) recipe).getRecipeType().equals(type)){
				if(type.getRecipeClass() != recipe.getClass()){
					throw new RuntimeException("Invalid recipe in " + type.getName());
				}
				//noinspection unchecked
				recipes.add((R) recipe);
			}
		}
		return Collections.unmodifiableList(recipes);
	}

	public static NonNullList<ItemStack> deserializeItems(JsonElement jsonObject){
		if(jsonObject.isJsonArray()){
			return SerializationUtil.stream(jsonObject.getAsJsonArray()).map(entry -> deserializeItem(entry.getAsJsonObject())).collect(NonNullListCollector.toList());
		} else {
			return NonNullList.from(deserializeItem(jsonObject.getAsJsonObject()));
		}
	}

	private static ItemStack deserializeItem(JsonObject jsonObject){
		ResourceLocation resourceLocation = new ResourceLocation(JsonUtils.getString(jsonObject, "item"));
		Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
		if(item == null){
			throw new IllegalStateException(resourceLocation + " did not exist");
		}
		int count = 1;
		if(jsonObject.has("count")){
			count = JsonUtils.getInt(jsonObject, "count");
		}
		//TODO support nbt
		return new ItemStack(item, count);
	}

}
