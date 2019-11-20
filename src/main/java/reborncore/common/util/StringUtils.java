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

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

/**
 * @author Prospector on 11/05/16
 */
public class StringUtils {

	public static String toFirstCapital(String input) {
		if (input == null || input.length() == 0) {
			return input;
		}

		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	public static String toFirstCapitalAllLowercase(String input) {
		if (input == null || input.length() == 0) {
			return input;
		}
		String output = input.toLowerCase();
		return output.substring(0, 1).toUpperCase() + output.substring(1);
	}
	
	/**
	 * Simple wrapper for text translation
	 * 
	 * @param translationKey String Translation Key from language file
	 * @return String Translated string
	 */
	public static String t(String translationKey) {
		return new TextComponentTranslation(translationKey).getFormattedText();
	}
	
	/**
	 * Simple wrapper for formatted text translation
	 * 
	 * @param translationKey String Translation Key from language file
	 * @param format Text to insert into translation
	 * @return String Translated string
	 */
	public static String t(String translationKey, Object... format) {
		return new TextComponentTranslation(translationKey, format).getFormattedText();
	}
	
	/**
	 * Returns red-yellow-green text formatting depending on percentage
	 * @param percentage int percentage amount
	 * @return TextFormatting Red or Yellow or Green
	 */
	public static TextFormatting getPercentageColour(int percentage) {
		if (percentage <= 10) {
			return TextFormatting.RED;
		} else if (percentage >= 75) {
			return TextFormatting.GREEN;
		} else {
			return TextFormatting.YELLOW;
		}
	}

}
