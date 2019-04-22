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

import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RecipeManager {

	private static final Map<ResourceLocation, RecipeType<?>> recipeTypes = new HashMap<>();

	public static <R extends Recipe> RecipeType<R> newRecipeType(Class<R> clazz, ResourceLocation name){
		if(recipeTypes.containsKey(name)){
			throw new RuntimeException("Recipe type with this name already registered");
		}
		RecipeType<R> type = new RecipeType<>(clazz, name);
		recipeTypes.put(name, type);

		RecipeSerializers.register(type);

		return type;
	}

	public static RecipeType<?> getRecipeType(ResourceLocation name){
		if(!recipeTypes.containsKey(name)){
			throw new RuntimeException("Recipe type " + name + " not found");
		}
		return recipeTypes.get(name);
	}

}
