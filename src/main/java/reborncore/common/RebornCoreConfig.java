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

import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.impl.ConfigRegistry;


@RebornRegistry
public class RebornCoreConfig {

	@ConfigRegistry(comment = "The Amount of FU to output from EU", category = "power")
	public static int euPerFU = 4;

	@ConfigRegistry(comment = "Contains some debug tools to help fix issues with ores.", category = "debug")
	public static boolean oreDebug = false;

	@ConfigRegistry(comment = "AutoUpdates none user chnaged config values, when the mod default changes.")
	public static boolean configUpdating = true;

	@ConfigRegistry(comment = "Wrench required to pick machine. If not wrenched than machine frame will drop instead.")
	public static boolean wrenchRequired = false;

	@ConfigRegistry(comment = "Show Stack Info HUD (ClientSideOnly)", category = "client")
	public static boolean ShowStackInfoHUD = true;

	@ConfigRegistry(comment = "Screen corner for HUD, 0 is top left, 1 is top right, 2 is bottom right and 3 is bottom left (ClientSideOnly)", category = "client")
	public static int stackInfoCorner = 0;

	@ConfigRegistry(comment = "X padding for HUD", category = "client")
	public static int stackInfoX = 2;

	@ConfigRegistry(comment = "Y padding for HUD", category = "client")
	public static int stackInfoY = 7;

	@ConfigRegistry(comment = "Enable Seasonal Easter Eggs", category = "client")
	public static boolean easterEggs = true;

}
