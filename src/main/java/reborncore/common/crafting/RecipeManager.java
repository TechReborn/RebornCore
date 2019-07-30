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

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import reborncore.common.crafting.ingredient.RebornIngredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeManager {

	private static final Map<Identifier, RebornRecipeType<?>> recipeTypes = new HashMap<>();

	public static <R extends RebornRecipe> RebornRecipeType<R> newRecipeType(Class<R> clazz, Identifier name){
		if(recipeTypes.containsKey(name)){
			throw new RuntimeException("RebornRecipe type with this name already registered");
		}
		RebornRecipeType<R> type = new RebornRecipeType<>(clazz, name);
		recipeTypes.put(name, type);

		Registry.register(Registry.RECIPE_SERIALIZER, name, (RecipeSerializer<?>) type);

		return type;
	}

	public static RebornRecipeType<?> getRecipeType(Identifier name){
		if(!recipeTypes.containsKey(name)){
			throw new RuntimeException("RebornRecipe type " + name + " not found");
		}
		return recipeTypes.get(name);
	}

	public static List<RebornRecipeType> getRecipeTypes(String namespace){
		return recipeTypes.values().stream().filter(rebornRecipeType -> rebornRecipeType.getName().getNamespace().equals(namespace)).collect(Collectors.toList());
	}

	public static void validateRecipes(World world){
		recipeTypes.forEach((key, value) -> validate(value, world));
	}

	private static void validate(RebornRecipeType<?> rebornRecipeType, World world){
		List<RebornRecipe> recipes = rebornRecipeType.getRecipes(world);

		for(RebornRecipe recipe1 : recipes){
			for(RebornRecipe recipe2 : recipes){
				if(recipe1 == recipe2){
					continue;
				}

				Validate.isTrue(recipe1.getRebornIngredients().size() > 0, recipe1.getId() + " has no inputs");
				Validate.isTrue(recipe2.getRebornIngredients().size() > 0, recipe2.getId() + " has no inputs");
				Validate.isTrue(recipe1.getOutputs().size() > 0, recipe1.getId() + " has no outputs");
				Validate.isTrue(recipe2.getOutputs().size() > 0, recipe2.getId() + " has no outputs");

				boolean hasAll = true;

				for(RebornIngredient recipe1Input : recipe1.getRebornIngredients()){
					boolean matches = false;
					for(ItemStack testStack : recipe1Input.getPreviewStacks()){
						for(RebornIngredient recipe2Input : recipe2.getRebornIngredients()){
							if(recipe2Input.test(testStack)){
								matches = true;
							}
						}
					}

					if(!matches){
						hasAll = false;
					}
				}

				if(hasAll){
					System.out.println(recipe1.getId() + " conflicts with " + recipe2.getId());
				}

			}
		}
	}


}
