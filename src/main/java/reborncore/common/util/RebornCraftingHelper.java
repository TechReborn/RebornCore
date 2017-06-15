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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RebornCraftingHelper {

	public static ResourceLocation getNameForRecipe(ItemStack output) {
		ModContainer activeContainer = Loader.instance().activeModContainer();
		ResourceLocation baseLoc = new ResourceLocation(activeContainer.getModId(), output.getItem().getRegistryName().getResourcePath());
		ResourceLocation recipeLoc = baseLoc;
		int index = 0;
		while (CraftingManager.REGISTRY.containsKey(recipeLoc)) {
			index++;
			recipeLoc = new ResourceLocation(activeContainer.getModId(), baseLoc.getResourcePath() + "_" + index);
		}
		return recipeLoc;
	}

	public static void addShapedOreRecipe(ItemStack outputItemStack, Object... objectInputs) {
		CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(objectInputs);
		ResourceLocation location = getNameForRecipe(outputItemStack);
		ShapedRecipes recipe = new ShapedRecipes(outputItemStack.getItem().getRegistryName().toString(), primer.width, primer.height, primer.input, outputItemStack);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}

	public static IRecipe addShapedRecipe(ItemStack output, Object... params) {
		ResourceLocation location = getNameForRecipe(output);
		ShapedRecipes recipe = new ShapedRecipes(output.getItem().getRegistryName().toString(), 3, 3, buildInput(params), output);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
		return recipe;
	}

	public static void addShapelessOreRecipe(ItemStack output, Object... input) {
		ResourceLocation location = getNameForRecipe(output);
		ShapelessOreRecipe recipe = new ShapelessOreRecipe(location, output, input);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}

	public static void addShapelessRecipe(ItemStack output, Object... input) {
		ResourceLocation location = getNameForRecipe(output);
		ShapelessRecipes recipe = new ShapelessRecipes(location.getResourceDomain(), output, buildInput(input));
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}

	private static NonNullList<Ingredient> buildInput (Object[] input) {
		NonNullList<Ingredient> list = NonNullList.create();
		for(Object obj : input){
			if(obj instanceof Ingredient){
				list.add((Ingredient) obj);
			} else {
				Ingredient ingredient = CraftingHelper.getIngredient(obj);
				if(ingredient == null){
					ingredient = Ingredient.EMPTY;
				}
				list.add(ingredient);
			}
		}
		return list;
	}

	public static void addSmelting(Block input, ItemStack output, float xp) {
		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(Item input, ItemStack output, float xp) {
		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(ItemStack input, ItemStack output, float xp) {
		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(ItemStack input, ItemStack output) {
		addSmelting(input, output, 1F);
	}

	public static void addSmelting(Item input, ItemStack output) {
		addSmelting(input, output, 1F);
	}

	public static void addSmelting(Block input, ItemStack output) {
		addSmelting(input, output, 1F);
	}
}
