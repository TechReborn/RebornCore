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

package reborncore.common.powerSystem;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import reborncore.RebornCore;
import reborncore.common.RebornCoreConfig;
import reborncore.common.util.serialization.SerializationUtil;

import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;

public class PowerSystem {
	public static File priorityConfig;
	private static int euPriority;
	private static int forgePriority;
	private static int euPriorityDefault = 0;
	private static int forgePriorityDefault = 1;
	private static final char[] magnitude = new char[] { 'k', 'M', 'G', 'T' };

	public static String getLocaliszedPower(double eu) {
		return getLocaliszedPower((int) eu);
	}

	public static String getLocaliszedPowerNoSuffix(double eu) {
		return getLocaliszedPowerNoSuffix((int) eu);
	}

	public static String getLocaliszedPowerFormatted(double eu) {
		return getLocaliszedPowerFormatted((int) eu);
	}

	public static String getLocaliszedPowerFormattedNoSuffix(double eu) {
		return getLocaliszedPowerFormattedNoSuffix((int) eu);
	}

	public static String getLocaliszedPower(int eu) {
		if (getDisplayPower() == EnergySystem.EU) {
			return getRoundedString(eu, EnergySystem.EU.abbreviation);
		} else {
			return getRoundedString(eu * RebornCoreConfig.euPerFU, getDisplayPower().abbreviation);
		}
	}

	public static String getLocaliszedPowerNoSuffix(int eu) {
		if (getDisplayPower() == EnergySystem.EU) {
			return getRoundedString(eu, "");
		} else {
			return getRoundedString(eu * RebornCoreConfig.euPerFU, "");
		}
	}

	public static String getLocaliszedPowerFormatted(int eu) {
		if (getDisplayPower() == EnergySystem.EU) {
			return getRoundedString(eu, EnergySystem.EU.abbreviation);
		} else {
			return getRoundedString(eu * RebornCoreConfig.euPerFU, getDisplayPower().abbreviation, true);
		}
	}

	public static String getLocaliszedPowerFormattedNoSuffix(int eu) {
		if (getDisplayPower() == EnergySystem.EU) {
			return getRoundedString(eu, "", true);
		} else {
			return getRoundedString(eu * RebornCoreConfig.euPerFU, "", true);
		}
	}

	private static String getRoundedString(int value, String units) {
		return getRoundedString(value, units, false);
	}

	private static String getRoundedString(int euValue, String units, boolean doFormat) {
		String ret = "";
		float value = 0f;
		int i = 0;
		boolean showMagnitude = true;
		if (euValue < 0) {
			ret = "-";
			euValue = -euValue;
		}

		if (euValue < 1000) {
			doFormat = false;
			showMagnitude = false;
			value = euValue;
		} else if (euValue >= 1000) {
			for (i = 0; ; i++) {
				if (euValue < 10000 && euValue % 1000 >= 100) {
					value = euValue / 1000;
					value += ((float)euValue % 1000) / 1000;
					break;
				}
				euValue /= 1000;
				if (euValue < 1000) {
					value = euValue;
					break;
				}
			}
		}

		if (FMLCommonHandler.instance().getEffectiveSide().isClient() && doFormat) {
			ret += NumberFormat
					.getNumberInstance(Locale.forLanguageTag(
							Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode()))
					.format(value);
		} else {
			ret += value;
		}

		if (showMagnitude) {
			ret += magnitude[i];
		}

		if (units != "") { ret += " " + units; }

		return ret;
	}

	public static EnergySystem getDisplayPower() {
		int eu = euPriority;
		int fe = forgePriority;
		if ( eu > fe && RebornCoreConfig.getRebornPower().eu())
			return EnergySystem.EU;
		return EnergySystem.FE;
	}

	public static void bumpPowerConfig() {
		EnergyPriorityConfig config = new EnergyPriorityConfig();
		if (getDisplayPower() == EnergySystem.EU) {
			config.setEuPriority(0);
			config.setForgePriority(1);
		} else if (getDisplayPower() == EnergySystem.FE) {
			config.setEuPriority(1);
			config.setForgePriority(0);
		}
		writeConfig(config);
	}

	public static void reloadConfig() {
		if (!priorityConfig.exists()) {
			writeConfig(new EnergyPriorityConfig());
		}
		if (priorityConfig.exists()) {
			EnergyPriorityConfig config = null;
			try (Reader reader = new FileReader(priorityConfig)) {
				config = SerializationUtil.GSON.fromJson(reader, EnergyPriorityConfig.class);
			} catch (Exception e) {
				e.printStackTrace();
				RebornCore.logHelper.error("Failed to read power config, will reset to defautls and save a new file.");
			}
			if (config == null) {
				config = new EnergyPriorityConfig();
				writeConfig(config);
			}
			euPriority = config.euPriority;
			forgePriority = config.forgePriority;
		}
	}

	public static void writeConfig(EnergyPriorityConfig config) {
		try (Writer writer = new FileWriter(priorityConfig)) {
			SerializationUtil.GSON.toJson(config, writer);
		} catch (Exception e) {

		}
		reloadConfig();
	}

	public enum EnergySystem {
		EU(0xFF800600, "EU", 43, 151, 0xFF670000),
		FE(0xFFBE281A, "FE", 15, 151, 0xFF960D0D);

		public int colour;
		public int altColour;
		public String abbreviation;
		public int xBar;
		public int yBar;

		EnergySystem(int colour, String abbreviation, int xBar, int yBar, int altColour) {
			this.colour = colour;
			this.abbreviation = abbreviation;
			this.xBar = xBar;
			this.yBar = yBar;
			this.altColour = altColour;
		}
	}

	public static class EnergyPriorityConfig {
		public int euPriority = euPriorityDefault;
		public int forgePriority = forgePriorityDefault;

		public int getEuPriority() {
			return euPriority;
		}

		public void setEuPriority(int euPriority) {
			this.euPriority = euPriority;
		}

		public int getForgePriority() {
			return forgePriority;
		}

		public void setForgePriority(int forgePriority) {
			this.forgePriority = forgePriority;
		}
	}
}
