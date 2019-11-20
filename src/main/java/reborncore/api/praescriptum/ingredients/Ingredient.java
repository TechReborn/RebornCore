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

package reborncore.api.praescriptum.ingredients;

/**
 * @author estebes
 */
public abstract class Ingredient<T> {
    public Ingredient(T ingredient) {
        this.ingredient = ingredient;
    }

    public abstract boolean isEmpty();

    public abstract boolean matches(Object other);

    public abstract boolean matchesStrict(Object other);

    public abstract String toFormattedString();

    @Override
    public boolean equals(Object object) {
        if (getClass() != object.getClass()) return false;

        return matches(((Ingredient<?>) object).ingredient);
    }

    @Override
    public String toString() {
        return String.format("Ingredient(%s)", toFormattedString());
    }

    // Fields >>
    public final T ingredient;
    // << Fields
}

