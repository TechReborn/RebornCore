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

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Mark on 20/02/2016.
 */
public class RebornCoreConfig {

	public static Configuration config;
	public static String CATEGORY_POWER = "power";
	public static String CATEGORY_MISC = "misc";
	public static int euPerFU;
	public static boolean ShowStackInfoHUD;
	public static int stackInfoCorner;
	public static int stackInfoX;
	public static int stackInfoY;
	public static boolean versionCheck;
	public static boolean easterEggs;
	public static boolean oreDebug;
	public static boolean configUpdating = true;
	private static RebornCoreConfig instance = null;
	public static boolean wrenchRequired = true;

	public RebornCoreConfig(File configFile) {
		config = new Configuration(configFile);
		config.load();

		RebornCoreConfig.Configs();

		config.save();
	}

	public static RebornCoreConfig initialize(File configFile) {

		if (instance == null)
			instance = new RebornCoreConfig(configFile);
		else
			throw new IllegalStateException("Cannot initialize TechReborn Config twice");

		return instance;
	}

	public static RebornCoreConfig instance() {
		if (instance == null) {

			throw new IllegalStateException("Instance of TechReborn Config requested before initialization");
		}
		return instance;
	}

	public static void Configs() {

		euPerFU = config.get(CATEGORY_POWER, "EU - FU ratio", 4, "The Amount of FU to output from EU")
				.getInt();

		versionCheck = config.get(CATEGORY_MISC, "Check for new versions", true, "Enable version checker")
				.getBoolean();

		ShowStackInfoHUD = config.get(CATEGORY_POWER, "Show Stack Info HUD", true, "Show Stack Info HUD (ClientSideOnly)")
				.getBoolean(true);
		
		stackInfoCorner = config.get(CATEGORY_POWER, "Stack Info Corner", 0, "Screen corner for HUD, 0 is top left, 1 is top right, 2 is bottom right and 3 is bottom left (ClientSideOnly)")
				.getInt();

		stackInfoX = config.get(CATEGORY_POWER, "Stack Info X", 2, "X padding for HUD (ClientSideOnly)")
				.getInt();
		
		stackInfoY = config.get(CATEGORY_POWER, "Stack Info Y", 7, "Y padding for HUD (ClientSideOnly)")
				.getInt();

		easterEggs = config.get(CATEGORY_MISC, "Enable Seasonal Easter Eggs", true, "Disable this is you don't want seasonal easter eggs")
				.getBoolean();

		oreDebug = config.get(CATEGORY_MISC, "Enable Debug tools for ores", false, "Contains some debug tools to help fix issues with ores.")
			.getBoolean();

		configUpdating = config.get(CATEGORY_MISC, "Config Updater", true, "AutoUpdates none user chnaged config values, when the mod default changes.")
			.getBoolean();


		wrenchRequired = config.get(CATEGORY_MISC, "Wrench required", true, "Wrench required to pick machine. If not wrenched than machine frame will drop instead.")
				.getBoolean(true);

	}

}
