/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
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
import reborncore.api.power.IPowerConfig;

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
	public static boolean stackInfoBottom;
	public static int stackInfoX;
	public static int stackInfoY;
	public static boolean versionCheck;
	public static boolean easterEggs;
	protected static boolean enableEU;
	protected static boolean enableTesla;
	protected static boolean enableForge;
	private static RebornCoreConfig instance = null;
	private static IPowerConfig powerConfig = null;
	public static boolean mtDocGen;
	public static boolean isIC2Loaded = false;

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

		enableTesla = config.get(CATEGORY_POWER, "Allow Tesla", false, "Allow machines to be powered with Tesla").getBoolean();

		enableForge = config.get(CATEGORY_POWER, "Allow Forge", true, "Allow machines to be powered with Forges power system").getBoolean();

		enableEU = config
			.get(CATEGORY_POWER, "Allow IC2 EU", true, "Allow machines to be powered with EU")
			.getBoolean();

		euPerFU = config.get(CATEGORY_POWER, "EU - FU ratio", 4, "The Amount of FU to output from EU").getInt();

		versionCheck = config.get(CATEGORY_MISC, "Check for new versions", true, "Enable version checker").getBoolean();

		ShowStackInfoHUD = config.get(CATEGORY_POWER, "Show Stack Info HUD", true, "Show Stack Info HUD (ClientSideOnly)")
			.getBoolean(true);
		stackInfoBottom = config.get(CATEGORY_POWER, "Stack Info Bottom", true, "Reverse the order of the HUD, and calculate it's X and Y positions from the bottom left corner (ClientSideOnly)")
			.getBoolean(true);

		stackInfoX = config.get(CATEGORY_POWER, "Stack Info X", 2, "X coordinate of the stack hud (ClientSideOnly)").getInt();
		stackInfoY = config.get(CATEGORY_POWER, "Stack Info Y", 7, "Y coordinate of the stack hud (ClientSideOnly)").getInt();

		easterEggs = config.get(CATEGORY_MISC, "Enable Seasonal Easter Eggs", true, "Disable this is you don't want seasonal easter eggs").getBoolean();

		mtDocGen = config.get(CATEGORY_MISC, "mtDocGen", false, "Enable automatic generation of MT docs at runtime (Beta)").getBoolean();
		//resets this when the config is reloaded
		powerConfig = null;
	}

	public static IPowerConfig getRebornPower() {
		if (powerConfig == null) {
			powerConfig = new IPowerConfig() {
				@Override
				public boolean eu() {
					return enableEU;
				}

				@Override
				public boolean tesla() {
					return enableTesla;
				}

				@Override
				public boolean internal() {
					return true;
				}

				@Override
				public boolean forge() {
					return enableForge;
				}
			};
		}
		return powerConfig;
	}
}
