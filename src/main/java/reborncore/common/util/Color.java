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


package reborncore.common.util;

/**
 * @author TeamC4
 */

@Deprecated
public enum Color {
	BLACK("\u00A70"), //
	DARK_BLUE("\u00A71"), //
	DARK_GREEN("\u00A72"), //
	DARK_AQUA("\u00A73"), //
	DARK_RED("\u00A74"), //
	DARK_PURPLE("\u00A75"), //
	GOLD("\u00A76"), //
	GRAY("\u00A77"), //
	DARK_GRAY("\u00A78"), //
	BLUE("\u00A79"), //
	GREEN("\u00A7a"), //
	AQUA("\u00A7b"), //
	RED("\u00A7c"), //
	LIGHT_PURPLE("\u00A7d"), //
	YELLOW("\u00A7e"), //
	WHITE("\u00A7f"), //

	STRIKE_THROUGH("\u00A7m"), //
	UNDERLINE("\u00A7n"), //
	BOLD("\u00A7l"), //
	RANDOM("\u00A7k"), //
	ITALIC("\u00A7o"), //
	RESET("\u00A7r"); //

	public String code = "";

	private Color(String code) {

		this.code = code;
	}

	@Override
	public String toString() {

		return code;
	}
}
