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

package reborncore.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

import java.util.List;

public class RecipeRemover {
	public static void removeShapedRecipes(List<ItemStack> removelist) {
		for (ItemStack stack : removelist)
			removeShapedRecipe(stack);
	}

	public static void removeAnyRecipe(ItemStack resultItem) {
		for (IRecipe tmpRecipe : CraftingManager.REGISTRY) {
			ItemStack recipeResult = tmpRecipe.getRecipeOutput();
			if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
				removeRecipe(tmpRecipe);
			}
		}
	}

	public static void removeShapedRecipe(ItemStack resultItem) {
		for (IRecipe tmpRecipe : CraftingManager.REGISTRY) {
			if (tmpRecipe instanceof ShapedRecipes) {
				ShapedRecipes recipe = (ShapedRecipes) tmpRecipe;
				ItemStack recipeResult = recipe.getRecipeOutput();
				if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
					removeRecipe(recipe);
				}
			}
		}
	}

	private static void removeRecipe(IRecipe recipe) {
		RegistryManager.ACTIVE.getRegistry(GameData.RECIPES).remove(recipe.getRegistryName());
	}
}
