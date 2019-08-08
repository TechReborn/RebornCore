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

package reborncore.common;

import reborncore.common.registration.RebornRegister;
import reborncore.common.registration.config.ConfigRegistry;

@RebornRegister("reborncore")
public class RebornCoreConfig {

	@ConfigRegistry(config = "power", key = "EU - FU ratio", comment = "The Amount of FU to output from EU")
	public static int euPerFU = 4;

	@ConfigRegistry(config = "client", key = "Stack Info Corner", comment = "Screen corner for HUD, 0 is top left, 1 is top right, 2 is bottom right and 3 is bottom left")
	public static int stackInfoCorner = 0;

	@ConfigRegistry(config = "client", key = "Stack Info X", comment = "X padding for HUD ")
	public static int stackInfoX = 2;

	@ConfigRegistry(config = "client", key = "Stack Info Y", comment = "Y padding for HUD ")
	public static int stackInfoY = 7;


	@ConfigRegistry(config = "misc", key = "Enable Seasonal Easter Eggs", comment = "Disable this is you don't want seasonal easter eggs")
	public static boolean easterEggs = true;

	@ConfigRegistry(config = "power", key = "Energy smoking", comment = "When enabled machines that try to insert power into a machine with a lower teir will smoke")
	public static boolean smokeHighTeir = false;

}
