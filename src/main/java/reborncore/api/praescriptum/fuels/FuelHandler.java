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

package reborncore.api.praescriptum.fuels;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
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
public class FuelHandler {
    public FuelHandler(String name) {
        if (StringUtils.isBlank(name)) throw new IllegalArgumentException("The fuel handler name cannot be blank");

        this.name = name;
        logger = LogManager.getLogger("team_reborn|Praescriptum|" + name);
    }

    /**
     * Add a new fuel to this handler.
     *
     * @return new fuel object for ease of use
     */
    public Fuel addFuel() {
        return new Fuel(this);
    }

    /**
     * Adds a fuel to this handler.
     *
     * @param fuel    The fuel
     * @param replace Replace conflicting existing fuels, not recommended, may be ignored
     * @return True on success, false otherwise, e.g. on conflicts
     */
    public boolean addFuel(Fuel fuel, boolean replace) {
        Objects.requireNonNull(fuel.getInputIngredients(), "The source is null");

        if (fuel.getInputIngredients().size() <= 0) throw new IllegalArgumentException("No sources");

        if (fuel.getEnergyOutput() <= 0.0D) throw new IllegalArgumentException("The output is 0");

        Queue<InputIngredient<?>> queueOfSources = new ArrayDeque<>();
        for (InputIngredient<?> ingredient : fuel.getInputIngredients()) {
            if (ingredient.isEmpty())
                logger.warn(String.format("The %s %s is invalid. Skipping...",
                        ingredient.getClass().getSimpleName(), ingredient.toFormattedString()));
            else
                queueOfSources.add(ingredient);
        }

        for (InputIngredient<?> ingredient : queueOfSources) {
            if (ingredient instanceof OreDictionaryInputIngredient) {
                if (OreDictionary.getOres(((OreDictionaryInputIngredient) ingredient).ingredient).isEmpty()) {
                    logger.warn(String.format("Skipping %s => %s due to the non existence of items that are registered to a provided ore type",
                            queueOfSources, fuel.getEnergyOutput()));

                    return false;
                }
            }
        }

        Fuel temp = getFuel(queueOfSources);

        if (temp != null) {
            if (replace) {
                do {
                    if (!removeFuel(queueOfSources))
                        logger.error(String.format("Something went wrong while removing the fuel with sources %s", queueOfSources));
                } while (getFuel(queueOfSources) != null);
            } else {
                logger.error(String.format("Skipping %s => %s due to duplicate source for %s (%s => %s)", queueOfSources,
                        fuel.getEnergyOutput(), queueOfSources, queueOfSources, fuel.getEnergyOutput()));
                return false;
            }
        }

        Fuel newFuel = addFuel()
                .addSources(queueOfSources)
                .withEnergyOutput(fuel.getEnergyOutput())
                .withMetadata(fuel.getMetadata());

        fuels.add(newFuel);

        return true;
    }

    /**
     * Get the fuel for the given ingredients.
     *
     * @param ingredients The ingredient list
     * @return The fuel if it exists or null otherwise
     */
    protected Fuel getFuel(Collection<InputIngredient<?>> ingredients) {
        for (Fuel fuel : fuels) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) continue;

            final Queue<InputIngredient<?>> adjusted = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                adjusted.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (adjusted.isEmpty()) return fuel;
        }

        return null;
    }

    /**
     * Find a matching fuel for the provided sources
     *
     * @param itemStack Fuel source item (not modified)
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findFuel(ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack)) return null;

        return cachedFuels.get(Collections.singletonList(ItemStackInputIngredient.copyOf(itemStack)));
    }

    /**
     * Find a matching fuel for the provided sources
     *
     * @param itemStacks Fuel source items (not modified)
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findFuel(Collection<ItemStack> itemStacks) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.copyOf(stack)); // map ItemStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching fuel

        return cachedFuels.get(ingredients);
    }

    /**
     * Find a matching fuel for the provided sources
     *
     * @param fluidStack Fuel source fluid (not modified)
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findFuel2(FluidStack fluidStack) {
        if (fluidStack.amount <= 0) return null;

        return cachedFuels.get(Collections.singletonList(FluidStackInputIngredient.copyOf(fluidStack)));
    }

    /**
     * Find a matching fuel for the provided sources
     *
     * @param fluidStacks Fuel source fluids (not modified)
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findFuel2(Collection<FluidStack> fluidStacks) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.copyOf(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching fuel

        return cachedFuels.get(ingredients);
    }

    /**
     * Find a matching fuel for the provided sources
     *
     * @param itemStacks  Fuel source items (not modified)
     * @param fluidStacks Fuel source fluids (not modified)
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findFuel3(Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.copyOf(stack)); // map ItemStacks

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.copyOf(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching fuel

        return cachedFuels.get(ingredients);
    }

    /**
     * Given the sources find and apply the fuel to said sources.
     *
     * @param itemStack Fuel source item (not modified)
     * @param simulate  If true the manager will accept partially missing ingredients or
     *                  ingredients with insufficient quantities. This is primarily used to check whether a
     *                  slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findAndApply(ItemStack itemStack, boolean simulate) {
        if (ItemUtils.isEmpty(itemStack)) return null;

        List<InputIngredient<?>> ingredients = Collections.singletonList(ItemStackInputIngredient.of(itemStack));

        Fuel fuel = cachedFuels.get(ingredients);

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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

            return fuel;
        }

        return null;
    }

    /**
     * Given the sources find and apply the fuel to said sources.
     *
     * @param itemStacks Fuel source items (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findAndApply(Collection<ItemStack> itemStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.of(stack)); // map ItemStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching fuel

        Fuel fuel = cachedFuels.get(ingredients);

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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

            return fuel;
        }

        return null;
    }

    /**
     * Given the sources find and apply the fuel to said sources.
     *
     * @param fluidStack Fuel source fluid (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findAndApply2(FluidStack fluidStack, boolean simulate) {
        if (fluidStack.amount <= 0) return null;

        List<InputIngredient<?>> ingredients = Collections.singletonList(FluidStackInputIngredient.of(fluidStack));

        Fuel fuel = cachedFuels.get(ingredients);

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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

            return fuel;
        }

        return null;
    }

    /**
     * Given the sources find and apply the fuel to said sources.
     *
     * @param fluidStacks Fuel source fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findAndApply2(Collection<FluidStack> fluidStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.of(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching fuel

        Fuel fuel = cachedFuels.get(ingredients);

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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

            return fuel;
        }

        return null;
    }

    /**
     * Given the sources find and apply the fuel to said sources.
     *
     * @param itemStacks  Fuel source items (not modified)
     * @param fluidStacks Fuel source fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return The fuel if it exists or null otherwise
     */
    public Fuel findAndApply3(Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.of(stack)); // map ItemStacks

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.of(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return null; // if the inputs are empty the is no matching fuel

        Fuel fuel = cachedFuels.get(ingredients);

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return null;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return null; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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

            return fuel;
        }

        return null;
    }

    /**
     * Given the sources and the fuel apply the fuel to said sources.
     *
     * @param fuel      The fuel
     * @param itemStack Fuel source item (not modified)
     * @param simulate  If true the manager will accept partially missing ingredients or
     *                  ingredients with insufficient quantities. This is primarily used to check whether a
     *                  slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply(Fuel fuel, ItemStack itemStack, boolean simulate) {
        if (ItemUtils.isEmpty(itemStack)) return false;

        List<InputIngredient<?>> ingredients = Collections.singletonList(ItemStackInputIngredient.of(itemStack));

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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
     * Given the sources and the fuel apply the fuel to said sources.
     *
     * @param fuel       The fuel
     * @param itemStacks Fuel source items (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply(Fuel fuel, Collection<ItemStack> itemStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.of(stack)); // map ItemStacks

        if (ingredients.isEmpty()) return false; // if the inputs are empty we cannot apply the fuel

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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
     * Given the sources and the fuel apply the fuel to said sources.
     *
     * @param fuel       The fuel
     * @param fluidStack Fuel source fluid (not modified)
     * @param simulate   If true the manager will accept partially missing ingredients or
     *                   ingredients with insufficient quantities. This is primarily used to check whether a
     *                   slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply2(Fuel fuel, FluidStack fluidStack, boolean simulate) {
        if (fluidStack.amount <= 0) return false;

        List<InputIngredient<?>> ingredients = Collections.singletonList(FluidStackInputIngredient.of(fluidStack));

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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
     * Given the sources and the fuel apply the fuel to said sources.
     *
     * @param fuel        The fuel
     * @param fluidStacks Fuel source fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply2(Fuel fuel, Collection<FluidStack> fluidStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.of(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return false; // if the inputs are empty we cannot apply the fuel

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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
     * Given the sources and the fuel apply the fuel to said sources.
     *
     * @param fuel        The fuel
     * @param itemStacks  Fuel source items (not modified)
     * @param fluidStacks Fuel source fluids (not modified)
     * @param simulate    If true the manager will accept partially missing ingredients or
     *                    ingredients with insufficient quantities. This is primarily used to check whether a
     *                    slot/tank/etc can accept the source while trying to supply a machine with resources
     * @return True if the operation was successful or false otherwise
     */
    public boolean apply3(Fuel fuel, Collection<ItemStack> itemStacks, Collection<FluidStack> fluidStacks, boolean simulate) {
        Queue<InputIngredient<?>> ingredients = new ArrayDeque<>();

        for (ItemStack stack : itemStacks)
            if (!ItemUtils.isEmpty(stack)) ingredients.add(ItemStackInputIngredient.of(stack)); // map ItemStacks

        for (FluidStack stack : fluidStacks)
            if (stack.amount <= 0) ingredients.add(FluidStackInputIngredient.of(stack)); // map FluidStacks

        if (ingredients.isEmpty()) return false; // if the inputs are empty we cannot apply the fuel

        if (fuel != null) {
            // check if everything need for the input is available in the input (ingredients + quantities)
            if (ingredients.size() != fuel.getInputIngredients().size()) return false;

            final Queue<InputIngredient<?>> queueA = new ArrayDeque<>(fuel.getInputIngredients());
            for (InputIngredient<?> entry : ingredients)
                queueA.removeIf(temp -> temp.matches(entry.ingredient) && entry.getCount() >= temp.getCount());

            if (!queueA.isEmpty()) return false; // the inputs did not match

            if (!simulate) {
                final Queue<InputIngredient<?>> queueB = new ArrayDeque<>(fuel.getInputIngredients());
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
     * Removes a fuel from this handler.
     *
     * @param fuel The fuel
     * @return True if the fuel has been removed or false otherwise
     */
    public boolean removeFuel(Fuel fuel) {
        if (fuel == null) return false;

        cachedFuels.invalidate(fuel); // remove from cache
        return fuels.remove(fuel);
    }

    /**
     * Removes a fuel from this handler.
     *
     * @param ingredients The source ingredients
     * @return True if a valid fuel has been found and removed or false otherwise
     */
    public boolean removeFuel(Collection<InputIngredient<?>> ingredients) {
        Fuel fuel = getFuel(ingredients);
        if (fuel == null) return false;

        cachedFuels.invalidate(ingredients); // remove from cache
        return fuels.remove(fuel);
    }

    /**
     * Get all the fuels from this handler
     *
     * @return A list with all the fuels
     */
    public Collection<Fuel> getFuels() {
        return fuels;
    }

    // Fields >>
    public final String name;

    protected final Logger logger;

    protected final Queue<Fuel> fuels = new ArrayDeque<>();

    protected final LoadingCache<Collection<InputIngredient<?>>, Fuel> cachedFuels =
            Caffeine.newBuilder()
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .maximumSize(100)
                    .build(this::getFuel);
    // << Fields
}
