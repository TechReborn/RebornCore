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

package reborncore.api.praescriptum.Utils;

import net.minecraftforge.fluids.Fluid;

import reborncore.api.praescriptum.fuels.Fuel;
import reborncore.api.praescriptum.fuels.FuelHandler;
import reborncore.api.praescriptum.ingredients.Ingredient;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.recipes.Recipe;
import reborncore.api.praescriptum.recipes.RecipeHandler;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author estebes
 */
public class IngredientUtils {
    /**
     * Evaluate if an ingredient is empty.
     *
     * @param <T> Type of the ingredient to be checked
     * @return A simple isEmpty() predicate
     */
    public static <T extends Ingredient<?>> Predicate<T> isIngredientEmpty() {
        return Ingredient::isEmpty;
    }

    /**
     * Evaluate if an ingredient is empty while also executing an arbitrary action.
     * This is useful if you want to perform some logging when an item is null.
     * Check {@link RecipeHandler#addRecipe(Recipe, boolean)} for an example.
     *
     * @param action The action to be executed
     * @param <T>    Type of the ingredient to be checked
     * @return A simple isEmpty() predicate that also executes the provided action
     */
    public static <T extends Ingredient<?>> Predicate<T> isIngredientEmpty(Consumer<T> action) {
        return ingredient -> {
            boolean isEmpty = ingredient.isEmpty();
            if (isEmpty)
                action.accept(ingredient);
            return !isEmpty;
        };
    }

    public static <T> Predicate<T> isPartOfRecipe(RecipeHandler recipeHandler) {
        return object -> {
            for (Recipe recipe : recipeHandler.getRecipes()) {
                for (InputIngredient<?> ingredient : recipe.getInputIngredients())
                    if (ingredient.matches(object)) return true;
            }

            return false;
        };
    }

    public static <T> Predicate<T> isPartOfFuel(FuelHandler fuelHandler) {
        return object -> {
            for (Fuel fuel : fuelHandler.getFuels()) {
                for (InputIngredient<?> ingredient : fuel.getInputIngredients())
                    return ingredient.matches(object);
            }

            return false;
        };
    }

    public static <T extends Fluid> Predicate<T> isFluidPartOfFuel(FuelHandler fuelHandler) {
        return object -> {
            for (Fuel fuel : fuelHandler.getFuels()) {
                for (InputIngredient<?> ingredient : fuel.getInputIngredients()) {
                    if (ingredient instanceof FluidStackInputIngredient
                            && ((FluidStackInputIngredient) ingredient).ingredient.getFluid() == object)
                        return true;
                }
            }

            return false;
        };
    }
}
