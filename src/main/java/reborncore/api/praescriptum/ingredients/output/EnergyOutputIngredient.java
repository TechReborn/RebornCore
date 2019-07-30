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

package reborncore.api.praescriptum.ingredients.output;

import org.apache.commons.lang3.ClassUtils;

/**
 * @author estebes
 */
public class EnergyOutputIngredient extends OutputIngredient<Double> {
	public static EnergyOutputIngredient of(Double ingredient) {
		return new EnergyOutputIngredient(ingredient);
	}

	public static EnergyOutputIngredient copyOf(Double ingredient) {
		return new EnergyOutputIngredient(ingredient);
	}

	protected EnergyOutputIngredient(Double ingredient) {
		super(ingredient);
	}

	@Override
	public OutputIngredient<Double> copy() {
		return of(ingredient);
	}

	@Override
	public boolean isEmpty() {
		return ingredient == null || ingredient == 0.0D;
	}

	@Override
	public boolean matches(Object other) {
		if (!(ClassUtils.isPrimitiveOrWrapper(Double.class))) return false;

		return ingredient.equals(other);
	}

	@Override
	public boolean matchesStrict(Object other) {
		return matches(other);
	}

	@Override
	public String toFormattedString() {
		return ingredient.toString();
	}
}