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

package reborncore.api.praescriptum.ingredients.input;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * @author estebes
 */
public class FluidInputIngredient extends InputIngredient<String> {
    public static FluidInputIngredient of(String ingredient) {
        return of(ingredient, 1);
    }

    public static FluidInputIngredient of(String ingredient, int amount) {
        return of(ingredient, amount, null);
    }

    public static FluidInputIngredient of(String ingredient, int amount, Integer meta) {
        return new FluidInputIngredient(ingredient, amount, meta);
    }

    protected FluidInputIngredient(String ingredient) {
        this(ingredient, 1);
    }

    protected FluidInputIngredient(String ingredient, int amount) {
        this(ingredient, amount, null);
    }

    protected FluidInputIngredient(String ingredient, int amount, Integer meta) {
        super(ingredient);

        this.amount = amount;
        this.meta = meta;
    }

    @Override
    public Object getUnspecific() {
        throw new UnsupportedOperationException("Operation not supported for this ingredient.");
    }

    @Override
    public InputIngredient<String> copy() {
        throw new UnsupportedOperationException("Operation not supported for this ingredient.");
    }

    @Override
    public boolean isEmpty() {
        return amount <= 0;
    }

    @Override
    public int getCount() {
        return amount;
    }

    @Override
    public void shrink(int amount) {
        throw new UnsupportedOperationException("Operation not supported for this ingredient.");
    }

    @Override
    public boolean matches(Object other) {
        if (!(other instanceof FluidStack)) return false;

        FluidStack fluidStack = (FluidStack) other;
        return ingredient.equals(FluidRegistry.getFluidName(fluidStack));
    }

    @Override
    public boolean matchesStrict(Object other) {
        return other instanceof String && ingredient.equals(other);
    }

    @Override
    public String toFormattedString() {
        return ingredient;
    }

    // Fields >>
    public final int amount;
    public final Integer meta;

    private List<FluidStack> equivalents;
    // << Fields
}
