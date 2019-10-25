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

package reborncore.api.praescriptum.recipes;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import reborncore.api.praescriptum.Utils.IngredientUtils;
import reborncore.api.praescriptum.Utils.LogUtils;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.output.OutputIngredient;
import reborncore.common.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author estebes
 */
public class RecipeHandler {
	/**
	 * Create a recipe for this handler.
	 *
	 * @return new recipe object for ease of use
	 */
	public Recipe createRecipe() {
		return new Recipe(this);
	}

	/**
	 * Adds a recipe to this handler.
	 *
	 * @param recipe The recipe
	 * @param replace Replace conflicting existing recipes, not recommended, may be ignored
	 * @return True on success, false otherwise, e.g. on conflicts
	 */
	public boolean addRecipe(Recipe recipe, boolean replace) {
		Objects.requireNonNull(recipe.getInputIngredients(), "The input input is null");

		if (recipe.getInputIngredients().size() <= 0) throw new IllegalArgumentException("No inputs");

		Objects.requireNonNull(recipe.getOutputIngredients(), "The input output is null");

		if (recipe.getOutputIngredients().size() <= 0) throw new IllegalArgumentException("No outputs");

		ImmutableList<InputIngredient<?>> listOfInputs = recipe.getInputIngredients().stream()
			.filter(IngredientUtils.isIngredientEmpty((ingredient) ->
				LogUtils.LOGGER.warn(String.format("The %s %s is invalid. Skipping...", ingredient.getClass().getSimpleName(), ingredient.toFormattedString()))))
			.collect(ImmutableList.toImmutableList());

		ImmutableList<OutputIngredient<?>> listOfOutputs = recipe.getOutputIngredients().stream()
			.filter(IngredientUtils.isIngredientEmpty((ingredient) ->
					LogUtils.LOGGER.warn(String.format("The %s %s is invalid. Skipping...", ingredient.getClass().getSimpleName(), ingredient.toFormattedString()))))
			.collect(ImmutableList.toImmutableList());

		Optional<Recipe> temp = getRecipe(listOfInputs);

		if (temp.isPresent()) {
			if (replace) {
				do {
					if (!removeRecipe(listOfInputs))
						LogUtils.LOGGER.error(String.format("Something went wrong while removing the recipe with inputs %s", listOfInputs));
				} while (getRecipe(listOfInputs).isPresent());
			} else {
				LogUtils.LOGGER.error(String.format("Skipping %s => %s due to duplicate input for %s (%s => %s)", listOfInputs, listOfOutputs, listOfInputs, listOfInputs, listOfOutputs));
				return false;
			}
		}

		Recipe newRecipe = createRecipe()
			.withInput(listOfInputs)
			.withOutput(listOfOutputs)
			.withMetadata(recipe.getMetadata());

		recipes.add(newRecipe);

		return true;
	}

	/**
	 * Get the recipe for the given ingredients.
	 *
	 * @param ingredients The ingredient list
	 * @return The recipe if it exists or empty otherwise
	 */
	protected Optional<Recipe> getRecipe(ImmutableList<InputIngredient<?>> ingredients) {
		return recipes.stream()
			.filter(recipe -> {
				final List<InputIngredient<?>> listA = new ArrayList<>(recipe.getInputIngredients());
				ingredients.forEach(entry ->
					listA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()));

				return listA.isEmpty();
			})
			.findAny();
	}
	
	/**
	 * Get the recipe for given outputs
	 * 
	 * @param output List of outputs
	 * @return The recipe if it exists or empty otherwise
	 */
	public Optional<Recipe> getRecipeByOutput(ImmutableList<OutputIngredient<?>> output) {
		return this.recipes.stream().filter((recipe) -> {
			List<OutputIngredient<?>> listA = new ArrayList<>(recipe.getOutputIngredients());
			output.forEach((entry) -> {
				listA.removeIf((temp) -> {
					return temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount();
				});
			});
			return listA.isEmpty();
		}).findAny();
	}

	/**
	 * Find a matching recipe for the provided inputs
	 *
	 * @param items
	 * @param fluids
	 * @return
	 */
	public Optional<Recipe> findRecipe(ImmutableList<ItemStack> items, ImmutableList<FluidStack> fluids) {
		Stream<ItemStackInputIngredient> itemIngredients = items.stream()
			.filter(stack -> !ItemUtils.isEmpty(stack))
			.map(ItemStackInputIngredient::copyOf); // map ItemStacks

		Stream<FluidStackInputIngredient> fluidIngredients = fluids.stream()
			.filter(stack -> stack.amount <= 0)
			.map(FluidStackInputIngredient::copyOf); // map FluidStacks

		ImmutableList<InputIngredient<?>> ingredients = Stream.concat(itemIngredients, fluidIngredients)
			.collect(ImmutableList.toImmutableList());

		return Optional.ofNullable(cachedRecipes.get(ingredients));
	}

	/**
	 * Given the inputs find and apply the recipe to said inputs.
	 *
	 * @param items Recipe input items (not modified)
	 * @param fluids Recipe input fluids (not modified)
	 * @param simulate If true the manager will accept partially missing ingredients or
	 * ingredients with insufficient quantities. This is primarily used to check whether a
	 * slot/tank/etc can accept the input while trying to supply a machine with resources
	 * @return Recipe result, or empty if none
	 */
	public Optional<Recipe> findAndApply(ImmutableList<ItemStack> items, ImmutableList<FluidStack> fluids, boolean simulate) {
		Stream<ItemStackInputIngredient> itemIngredients = items.stream()
			.filter(stack -> !ItemUtils.isEmpty(stack))
			.map(ItemStackInputIngredient::of); // map ItemStacks

		Stream<FluidStackInputIngredient> fluidIngredients = fluids.stream()
			.filter(stack -> stack.amount <= 0)
			.map(FluidStackInputIngredient::of); // map FluidStacks

		ImmutableList<InputIngredient<?>> ingredients = Stream.concat(itemIngredients, fluidIngredients)
			.collect(ImmutableList.toImmutableList());

		if (ingredients.isEmpty()) return Optional.empty(); // if the inputs are empty we can return nothing

		Optional<Recipe> ret = Optional.ofNullable(cachedRecipes.get(ingredients));

		ret.map(recipe -> {
			// check if everything need for the input is available in the input (ingredients + quantities)
			if (ingredients.size() != recipe.getInputIngredients().size()) return Optional.empty();

			final List<InputIngredient<?>> listA = new ArrayList<>(recipe.getInputIngredients());
			ingredients.forEach(entry ->
				listA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()));

			if (!listA.isEmpty()) return Optional.empty(); // the input did not match

			if (!simulate) {
				final List<InputIngredient<?>> listB = new ArrayList<>(recipe.getInputIngredients());
				ingredients.forEach(entry ->
					listB.removeIf(temp -> {
						if (temp.matches(entry.ingredient)) {
							entry.shrink(temp.getCount()); // adjust the quantity
							return true;
						}
						return false;
					})
				);
			}

			return Optional.of(recipe);
		});

		return ret;
	}

	/**
	 * Given the inputs and the recipe apply the recipe to said inputs.
	 *
	 * @param recipe The recipe
	 * @param items Recipe input items (not modified)
	 * @param fluids Recipe input fluids (not modified)
	 * @param simulate If true the manager will accept partially missing ingredients or
	 * ingredients with insufficient quantities. This is primarily used to check whether a
	 * slot/tank/etc can accept the input while trying to supply a machine with resources
	 * @return True if the operation was successful or false otherwise
	 */
	public boolean apply(Recipe recipe, ImmutableList<ItemStack> items, ImmutableList<FluidStack> fluids, boolean simulate) {
		Stream<ItemStackInputIngredient> itemIngredients = items.stream()
			.filter(stack -> !ItemUtils.isEmpty(stack))
			.map(ItemStackInputIngredient::of); // map ItemStacks

		Stream<FluidStackInputIngredient> fluidIngredients = fluids.stream()
			.filter(stack -> stack.amount <= 0)
			.map(FluidStackInputIngredient::of); // map FluidStacks

		ImmutableList<InputIngredient<?>> ingredients = Stream.concat(itemIngredients, fluidIngredients)
			.collect(ImmutableList.toImmutableList());

		// check if everything need for the input is available in the input (ingredients + quantities)
		if (ingredients.size() != recipe.getInputIngredients().size()) return false;

		final List<InputIngredient<?>> listA = new ArrayList<>(recipe.getInputIngredients());
		ingredients.forEach(entry ->
			listA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()));

		if (!listA.isEmpty()) return false; // the input did not match

		if (!simulate) {
			final List<InputIngredient<?>> listB = new ArrayList<>(recipe.getInputIngredients());
			ingredients.forEach(entry ->
				listB.removeIf(temp -> {
					if (temp.matches(entry.ingredient)) {
						entry.shrink(temp.getCount()); // adjust the quantity
						return true;
					}
					return false;
				})
			);
		}

		return true;
	}

	/**
	 * Removes a recipe from this handler.
	 *
	 * @param recipe The recipe
	 * @return True if the recipe has been removed or false otherwise
	 */
	public boolean removeRecipe(Recipe recipe) {
		if (recipe == null) return false;

		cachedRecipes.invalidate(recipe); // remove from cache
		return recipes.remove(recipe);
	}

	/**
	 * Removes a recipe from this handler.
	 *
	 * @param ingredients The input ingredients
	 * @return True if a valid recipe has been found and removed or false otherwise
	 */
	public boolean removeRecipe(ImmutableList<InputIngredient<?>> ingredients) {
		Recipe recipe = getRecipe(ingredients).orElse(null);
		if (recipe == null) return false;

		cachedRecipes.invalidate(ingredients); // remove from cache
		return recipes.remove(recipe);
	}

	/**
	 * Get all the recipes from this handler
	 *
	 * @return A list with all the recipes
	 */
	public List<Recipe> getRecipes() {
		return recipes;
	}

	// Fields >>
	protected final List<Recipe> recipes = new ArrayList<>();

	protected final LoadingCache<ImmutableList<InputIngredient<?>>, Recipe> cachedRecipes =
		Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.maximumSize(100)
			.build(ingredients ->
				recipes.stream()
					.filter(recipe -> {
						final List<InputIngredient<?>> listA = new ArrayList<>(recipe.getInputIngredients());
						ingredients.forEach(entry ->
							listA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()));

						return listA.isEmpty();
					})
					.findAny()
					.orElse(null)
			);
	// << Fields
}