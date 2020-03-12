/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
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

package reborncore.common;

import reborncore.common.config.Config;

public class RebornCoreConfig {

	@Config(config = "client", key = "Show stack info", comment = "Show additional stack info on HUD, like active, charge, etc.")
	public static boolean ShowStackInfoHUD = true;

	@Config(config = "client", key = "Stack Info Corner", comment = "Screen corner for HUD, 0 is top left, 1 is top right, 2 is bottom right and 3 is bottom left")
	public static int stackInfoCorner = 0;

	@Config(config = "client", key = "Stack Info X", comment = "X padding for HUD ")
	public static int stackInfoX = 2;

	@Config(config = "client", key = "Stack Info Y", comment = "Y padding for HUD ")
	public static int stackInfoY = 7;


	@Config(config = "misc", key = "Enable Seasonal Easter Eggs", comment = "Disable this is you don't want seasonal easter eggs")
	public static boolean easterEggs = true;

	@Config(config = "power", key = "Energy smoking", comment = "When enabled machines that try to insert power into a machine with a lower teir will smoke")
	public static boolean smokeHighTeir = false;

}
