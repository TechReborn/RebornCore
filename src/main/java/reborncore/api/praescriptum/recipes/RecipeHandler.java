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

package reborncore.api.praescriptum.recipes;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import reborncore.api.praescriptum.ingredients.output.OutputIngredient;
import reborncore.common.util.FluidUtils;
import reborncore.common.util.ItemUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

/**
 * @author estebes
 */
public class RecipeHandler {
    public RecipeHandler(String name) {
        if (StringUtils.isBlank(name)) throw new IllegalArgumentException("The recipe handler name cannot be blank");

        this.name = name;
        logger = LogManager.getLogger("team_reborn|Praescriptum|" + name);
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

        if (recipe.getItemOutputs().length <= 0 && recipe.getFluidOutputs().length <= 0)
            throw new IllegalArgumentException("No outputs");

        Queue<InputIngredient<?>> queueOfInputs = new ArrayDeque<>();
        for (InputIngredient<?> ingredient : recipe.getInputIngredients()) {
            if (ingredient.isEmpty()) {
                logger.warn(String.format("%s: The %s %s is invalid. Skipping...", name,
                        ingredient.getClass().getSimpleName(), ingredient.toFormattedString()));
                return false;
            } else {
                queueOfInputs.add(ingredient);
            }
        }

        for (ItemStack itemOutput : recipe.getItemOutputs()) {
            if (ItemUtils.isEmpty(itemOutput)) {
                logger.warn(String.format("%s: The ItemStack %s is invalid. Skipping...", name,
                        ItemUtils.toFormattedString(itemOutput)));
                return false;
            }
        }

        for (FluidStack fluidOutput : recipe.getFluidOutputs()) {
            if (fluidOutput.getFluid() == null || fluidOutput.amount <=0) {
                logger.warn(String.format("%s: The FluidStack %s is invalid. Skipping...", name,
                        FluidUtils.toFormattedString(fluidOutput)));
                return false;
            }
        }

        for (InputIngredient<?> inputIngredient : queueOfInputs) {
            if (inputIngredient instanceof OreDictionaryInputIngredient) {
                if (OreDictionary.getOres(((OreDictionaryInputIngredient) inputIngredient).ingredient).isEmpty()) {
                    logger.warn(String.format("%s: Skipping a recipe with input %s " +
                                    "due to the non existence of items that are registered to a provided ore type",
                            name, inputIngredient.ingredient));
                    return false;
                }
            }
        }

        Recipe temp;
        if (recipe.shouldUseNBT()) {
        	temp = getRecipeStrict(queueOfInputs);
        }
        else {
        	temp = getRecipe(queueOfInputs);
        }


        if (temp != null) {
            if (replace) {
                do {
                    if (!removeRecipe(queueOfInputs))
                        logger.error(String.format("%s: Something went wrong while removing the recipe with inputs %s", name, queueOfInputs));
                } while (getRecipe(queueOfInputs) != null);
            } else {
                logger.error(String.format("%s: Skipping recipe with input %s due to the existence of an equal input", name, queueOfInputs));
                return false;
            }
        }

        Recipe newRecipe = createRecipe()
                .withInput(queueOfInputs)
                .withOutput(recipe.getItemOutputs(), recipe.getFluidOutputs())
                .withMetadata(recipe.getMetadata());

        recipes.add(newRecipe);

        return true;
    }

    /**
     * Get the recipe for the given input ingredients.
     *
     * @param ingredients The ingredient list
     * @return The recipe if it exists or null otherwise
     */
    public Recipe getRecipe(Collection<InputIngredient<?>> ingredients) {
        for (Recipe recipe : recipes) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) continue;

            final Queue<InputIngredient<?>> adjusted = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                adjusted.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (adjusted.isEmpty()) return recipe;
        }

        return null;
    }
    
    /**
     * Get the recipe for the given input ingredients including NBT match.
     *
     * @param ingredients The ingredient list
     * @return The recipe if it exists or null otherwise
     */
    public Recipe getRecipeStrict(Collection<InputIngredient<?>> ingredients) {
        for (Recipe recipe : recipes) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) continue;

            final Queue<InputIngredient<?>> adjusted = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                adjusted.removeIf(temp -> temp.matchesStrict(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (adjusted.isEmpty()) return recipe;
        }

        return null;
    }

    /**
     * Get the recipe for the given output ingredients.
     *
     * @param ingredients The ingredient list
     * @return The recipe if it exists or null otherwise
     */
	public Recipe getRecipeByOutput(Collection<OutputIngredient<?>> ingredients) {

		for (Recipe recipe : recipes) {
			// check if everything need for the output is available in the output
			// (ingredients + quantities)
			if (ingredients.size() != recipe.getItemOutputs().length) {
				continue;
			}

			final Queue<ItemStack> adjusted = new LinkedList<>(Arrays.asList(recipe.getItemOutputs()));
			for (OutputIngredient<?> entry : ingredients)
				adjusted.removeIf(temp -> entry.matches(temp) && entry.getCount() >= temp.getCount());

			if (adjusted.isEmpty()) {
				return recipe;
			}
		}

		return null;
	}

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param itemStack Recipe input item (not modified)
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findRecipe(ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack)) return null;

        return cachedRecipes.get(Collections.singletonList(ItemStackInputIngredient.copyOf(itemStack)));
    }

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param itemStacks Recipe input items (not modified)
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findRecipe(Collection<ItemStack> itemStacks) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.copyOf(stack)); // map ItemStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching recipe

        return cachedRecipes.get(ingredients);
    }

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param fluidStack Recipe input fluid (not modified)
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findRecipe2(FluidStack fluidStack) {
        if (fluidStack.amount <= 0) return null;

        return cachedRecipes.get(Collections.singletonList(FluidStackInputIngredient.copyOf(fluidStack)));
    }

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param fluidStacks Recipe input fluids (not modified)
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findRecipe2(Collection<FluidStack> fluidStacks) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.copyOf(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching recipe

        return cachedRecipes.get(ingredients);
    }

    /**
     * Find a matching recipe for the provided inputs
     *
     * @param itemStacks  Recipe input items (not modified)
     * @param fluidStacks Recipe input fluids (not modified)
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findRecipe3(Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.copyOf(stack)); // map ItemStacks

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.copyOf(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching recipe

        return cachedRecipes.get(ingredients);
    }

    /**
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param itemStack Recipe input item (not modified)
     * @param simulate  If true the manager will accept partially missing ingredients or
     *                  ingredients with insufficient quantities. This is primarily used to check whether a
     *                  slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findAndApply(ItemStack itemStack, boolean simulate) {
        if (ItemUtils.isEmpty(itemStack)) return null;

        List<InputIngredient<?>> ingredients = Collections.singletonList(ItemStackInputIngredient.of(itemStack));

        Recipe recipe = cachedRecipes.get(ingredients);

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return recipe;
        }

        return null;
    }

    /**
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param itemStacks Recipe input items (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findAndApply(Collection<ItemStack> itemStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.of(stack)); // map ItemStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching recipe

        Recipe recipe = cachedRecipes.get(ingredients);

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return recipe;
        }

        return null;
    }

    /**
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param fluidStack Recipe input fluid (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findAndApply2(FluidStack fluidStack, boolean simulate) {
        if (fluidStack.amount <= 0) return null;

        List<InputIngredient<?>> ingredients = Collections.singletonList(FluidStackInputIngredient.of(fluidStack));

        Recipe recipe = cachedRecipes.get(ingredients);

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return recipe;
        }

        return null;
    }

    /**
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param fluidStacks Recipe input fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findAndApply2(Collection<FluidStack> fluidStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.of(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching recipe

        Recipe recipe = cachedRecipes.get(ingredients);

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return recipe;
        }

        return null;
    }

    /**
     * Given the inputs find and apply the recipe to said inputs.
     *
     * @param itemStacks  Recipe input items (not modified)
     * @param fluidStacks Recipe input fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the input while trying to supply a machine with resources
     * @return The recipe if it exists or null otherwise
     */
    public Recipe findAndApply3(Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.of(stack)); // map ItemStacks

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.of(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching recipe

        Recipe recipe = cachedRecipes.get(ingredients);

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return recipe;
        }

        return null;
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

        List<InputIngredient<?>> ingredients = Collections.singletonList(ItemStackInputIngredient.of(itemStack));

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return true;
        }

        return false;
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
    public boolean apply(Recipe recipe, Collection<ItemStack> itemStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.of(stack)); // map ItemStacks

        if (ingredients.isEmpty()) return false; // if the inputs are empty we cannot apply the recipe

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return true;
        }

        return false;
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

        List<InputIngredient<?>> ingredients = Collections.singletonList(FluidStackInputIngredient.of(fluidStack));

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return true;
        }

        return false;
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
    public boolean apply2(Recipe recipe, Collection<FluidStack> fluidStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.of(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return false; // if the inputs are empty we cannot apply the recipe

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return true;
        }

        return false;
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
    public boolean apply3(Recipe recipe, Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.of(stack)); // map ItemStacks

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.of(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return false; // if the inputs are empty we cannot apply the recipe

        if (recipe != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != recipe.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(recipe.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(recipe.getInputIngredients());
                for (InputIngredient<?> entry : ingredients) {
                    queueB.removeIf(temp -> {
                        if (temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount()) {
                            entry.shrink(temp.getCount()); // adjust the quantity
                            return true;
                        }

                        return false;
                    });
                }
            }

            return true;
        }

        return false;
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
    public boolean removeRecipe(Collection<InputIngredient<?>> ingredients) {
        Recipe recipe = getRecipe(ingredients);
        if (recipe == null) return false;

        cachedRecipes.invalidate(ingredients); // remove from cache
        return recipes.remove(recipe);
    }

    /**
     * Get all the recipes from this handler
     *
     * @return A list with all the recipes
     */
    public Collection<Recipe> getRecipes() {
        return recipes;
    }

    // Fields >>
    // Fields >>
    public final String name;

    protected final Logger logger;

    protected final Queue<Recipe> recipes = new ArrayDeque<>();

    protected final LoadingCache<Collection<InputIngredient<?>>, Recipe> cachedRecipes =
            Caffeine.newBuilder()
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .maximumSize(100)
                    .build(this::getRecipe);
    // << Fields
}