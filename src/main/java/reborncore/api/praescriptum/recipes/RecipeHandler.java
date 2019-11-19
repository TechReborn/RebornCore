/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.api.praescriptum.recipes;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import reborncore.api.praescriptum.Utils.IngredientUtils;
import reborncore.api.praescriptum.Utils.LogUtils;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import reborncore.api.praescriptum.ingredients.output.OutputIngredient;
import reborncore.common.util.ItemUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

/**
 * @author estebes
 */
public class RecipeHandler {
    public RecipeHandler(String name) {
        if (StringUtils.isBlank(name)) throw new IllegalArgumentException("The recipe handler name cannot be blank");

        this.name = name;
    }

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
     * @param recipe  The recipe
     * @param replace Replace conflicting existing recipes, not recommended, may be ignored
     * @return True on success, false otherwise, e.g. on conflicts
     */
    public boolean addRecipe(Recipe recipe, boolean replace) {
        Objects.requireNonNull(recipe.getInputIngredients(), "The input is null");

        if (recipe.getInputIngredients().size() <= 0) throw new IllegalArgumentException("No inputs");

        Objects.requireNonNull(recipe.getOutputIngredients(), "The output is null");

        if (recipe.getOutputIngredients().size() <= 0) throw new IllegalArgumentException("No outputs");

        ImmutableList<InputIngredient<?>> listOfInputs = recipe.getInputIngredients().stream()
                .filter(IngredientUtils.isIngredientEmpty((ingredient) ->
                        LogUtils.LOGGER.warn(String.format("%s: The %s %s is invalid. Skipping...", name,
                                ingredient.getClass().getSimpleName(), ingredient.toFormattedString()))))
                .collect(ImmutableList.toImmutableList());

        ImmutableList<OutputIngredient<?>> listOfOutputs = recipe.getOutputIngredients().stream()
                .filter(IngredientUtils.isIngredientEmpty((ingredient) ->
                        LogUtils.LOGGER.warn(String.format("%s: The %s %s is invalid. Skipping...", name,
                                ingredient.getClass().getSimpleName(), ingredient.toFormattedString()))))
                .collect(ImmutableList.toImmutableList());

        boolean canBeSkipped = listOfInputs.stream()
                .filter(ingredient -> ingredient instanceof OreDictionaryInputIngredient)
                .anyMatch(ingredient -> OreDictionary.getOres(((OreDictionaryInputIngredient) ingredient).ingredient).isEmpty());

        if (canBeSkipped) {
            LogUtils.LOGGER.warn(String.format("%s: Skipping %s => %s due to the non existence of items that are registered to a provided ore type",
                    name, listOfInputs, listOfOutputs));

            return false;
        }

        Optional<Recipe> temp = getRecipe(listOfInputs);

        if (temp.isPresent()) {
            if (replace) {
                do {
                    if (!removeRecipe(listOfInputs))
                        LogUtils.LOGGER.error(String.format("%s: Something went wrong while removing the recipe with inputs %s", name, listOfInputs));
                } while (getRecipe(listOfInputs).isPresent());
            } else {
                LogUtils.LOGGER.error(String.format("%s: Skipping %s => %s due to duplicate input for %s (%s => %s)", listOfInputs,
                        name, listOfOutputs, listOfInputs, listOfInputs, listOfOutputs));
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
            output.forEach((entry) ->
                    listA.removeIf((temp) -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()));

            return listA.isEmpty();
        }).findAny();
    }

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param itemStack Recipe input item (not modified)
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findRecipe(ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack)) return Optional.empty();

        ImmutableList<InputIngredient<?>> ingredients = ImmutableList.of(ItemStackInputIngredient.copyOf(itemStack));
        return Optional.ofNullable(cachedRecipes.get(ingredients));
    }

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param itemStacks Recipe input items (not modified)
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findRecipe(ImmutableList<ItemStack> itemStacks) {
        ImmutableList<InputIngredient<?>> ingredients = itemStacks.stream()
                .filter(stack -> !ItemUtils.isEmpty(stack))
                .map(ItemStackInputIngredient::copyOf)
                .collect(ImmutableList.toImmutableList());

        return Optional.ofNullable(cachedRecipes.get(ingredients));
    }

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param fluidStack Recipe input fluid (not modified)
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findRecipe2(FluidStack fluidStack) {
        if (fluidStack.amount <= 0) return Optional.empty();

        ImmutableList<InputIngredient<?>> ingredients = ImmutableList.of(FluidStackInputIngredient.copyOf(fluidStack));
        return Optional.ofNullable(cachedRecipes.get(ingredients));
    }

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param fluidStacks Recipe input fluids (not modified)
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findRecipe2(ImmutableList<FluidStack> fluidStacks) {
        ImmutableList<InputIngredient<?>> ingredients = fluidStacks.stream()
                .filter(stack -> stack.amount <= 0)
                .map(FluidStackInputIngredient::copyOf)
                .collect(ImmutableList.toImmutableList());

        return Optional.ofNullable(cachedRecipes.get(ingredients));
    }

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param itemStacks  Recipe input items (not modified)
     * @param fluidStacks Recipe input fluids (not modified)
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findRecipe3(ImmutableList<ItemStack> itemStacks, ImmutableList<FluidStack> fluidStacks) {
        Stream<ItemStackInputIngredient> itemIngredients = itemStacks.stream()
                .filter(stack -> !ItemUtils.isEmpty(stack))
                .map(ItemStackInputIngredient::copyOf); // map ItemStacks

        Stream<FluidStackInputIngredient> fluidIngredients = fluidStacks.stream()
                .filter(stack -> stack.amount <= 0)
                .map(FluidStackInputIngredient::copyOf); // map FluidStacks

        ImmutableList<InputIngredient<?>> ingredients = Stream.concat(itemIngredients, fluidIngredients)
                .collect(ImmutableList.toImmutableList());

        return Optional.ofNullable(cachedRecipes.get(ingredients));
    }

    /**
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param itemStack Recipe input item (not modified)
     * @param simulate  If true the manager will accept partially missing ingredients or
     *                  ingredients with insufficient quantities. This is primarily used to check whether a
     *                  slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findAndApply(ItemStack itemStack, boolean simulate) {
        if (ItemUtils.isEmpty(itemStack)) return Optional.empty();

        ImmutableList<InputIngredient<?>> ingredients = ImmutableList.of(ItemStackInputIngredient.of(itemStack));

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
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param itemStacks Recipe input items (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findAndApply(ImmutableList<ItemStack> itemStacks, boolean simulate) {
        ImmutableList<InputIngredient<?>> ingredients = itemStacks.stream()
                .filter(stack -> !ItemUtils.isEmpty(stack))
                .map(ItemStackInputIngredient::of)
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
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param fluidStack Recipe input fluid (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findAndApply2(FluidStack fluidStack, boolean simulate) {
        if (fluidStack.amount <= 0) return Optional.empty();

        ImmutableList<InputIngredient<?>> ingredients = ImmutableList.of(FluidStackInputIngredient.of(fluidStack));

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
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param fluidStacks Recipe input fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findAndApply2(ImmutableList<FluidStack> fluidStacks, boolean simulate) {
        ImmutableList<InputIngredient<?>> ingredients = fluidStacks.stream()
                .filter(stack -> stack.amount <= 0)
                .map(FluidStackInputIngredient::of)
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
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param itemStacks  Recipe input items (not modified)
     * @param fluidStacks Recipe input fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return Recipe result, or empty if none
     */
    public Optional<Recipe> findAndApply3(ImmutableList<ItemStack> itemStacks, ImmutableList<FluidStack> fluidStacks, boolean simulate) {
        Stream<ItemStackInputIngredient> itemIngredients = itemStacks.stream()
                .filter(stack -> !ItemUtils.isEmpty(stack))
                .map(ItemStackInputIngredient::of); // map ItemStacks

        Stream<FluidStackInputIngredient> fluidIngredients = fluidStacks.stream()
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
     * @param recipe    The recipe
     * @param itemStack Recipe input item (not modified)
     * @param simulate  If true the manager will accept partially missing ingredients or
     *                  ingredients with insufficient quantities. This is primarily used to check whether a
     *                  slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply(Recipe recipe, ItemStack itemStack, boolean simulate) {
        if (ItemUtils.isEmpty(itemStack)) return false;

        ImmutableList<InputIngredient<?>> ingredients = ImmutableList.of(ItemStackInputIngredient.of(itemStack));

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
     * Given the inputs and the recipe apply the recipe to said inputs.
     *
     * @param recipe     The recipe
     * @param itemStacks Recipe input items (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply(Recipe recipe, ImmutableList<ItemStack> itemStacks, boolean simulate) {
        ImmutableList<InputIngredient<?>> ingredients = itemStacks.stream()
                .filter(stack -> !ItemUtils.isEmpty(stack))
                .map(ItemStackInputIngredient::of)
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
     * Given the inputs and the recipe apply the recipe to said inputs.
     *
     * @param recipe     The recipe
     * @param fluidStack Recipe input fluid (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply2(Recipe recipe, FluidStack fluidStack, boolean simulate) {
        if (fluidStack.amount <= 0) return false;

        ImmutableList<InputIngredient<?>> ingredients = ImmutableList.of(FluidStackInputIngredient.of(fluidStack));

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
     * Given the inputs and the recipe apply the recipe to said inputs.
     *
     * @param recipe      The recipe
     * @param fluidStacks Recipe input fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply2(Recipe recipe, ImmutableList<FluidStack> fluidStacks, boolean simulate) {
        ImmutableList<InputIngredient<?>> ingredients = fluidStacks.stream()
                .filter(stack -> stack.amount <= 0)
                .map(FluidStackInputIngredient::of)
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
     * Given the inputs and the recipe apply the recipe to said inputs.
     *
     * @param recipe      The recipe
     * @param itemStacks  Recipe input items (not modified)
     * @param fluidStacks Recipe input fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply3(Recipe recipe, ImmutableList<ItemStack> itemStacks, ImmutableList<FluidStack> fluidStacks, boolean simulate) {
        Stream<ItemStackInputIngredient> itemIngredients = itemStacks.stream()
                .filter(stack -> !ItemUtils.isEmpty(stack))
                .map(ItemStackInputIngredient::of); // map ItemStacks

        Stream<FluidStackInputIngredient> fluidIngredients = fluidStacks.stream()
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
    public final String name;

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