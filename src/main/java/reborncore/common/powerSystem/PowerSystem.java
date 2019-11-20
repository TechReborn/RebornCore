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


package reborncore.common.powerSystem;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.io.FileUtils;
import reborncore.common.RebornCoreConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;

public class PowerSystem {
	public static File selectedFile;
	private static EnergySystem selectedSystem = EnergySystem.values()[0];

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
		if(!selectedSystem.enabled.get()){
			bumpPowerConfig();
		}
		return selectedSystem;
	}

	public static void bumpPowerConfig() {
		int value = selectedSystem.ordinal() + 1;
		if(value == EnergySystem.values().length){
			value = 0;
		}
		selectedSystem = EnergySystem.values()[value];
		writeFile();
	}

	public static void readFile() {
		if (!selectedFile.exists()) {
			writeFile();
		}
		if (selectedFile.exists()) {

			try {
				String value = FileUtils.readFileToString(selectedFile, StandardCharsets.UTF_8);
				selectedSystem = Arrays.stream(EnergySystem.values()).filter(energySystem -> energySystem.abbreviation.equalsIgnoreCase(value)).findFirst().orElse(EnergySystem.values()[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeFile() {
		try {
			FileUtils.write(selectedFile, selectedSystem.abbreviation, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		readFile();
	}

	public enum EnergySystem {
		FE(0xFFBE281A, "FE", 113, 151, 0xFF960D0D),
		EU(0xFF800600, "EU", 141, 151, 0xFF670000);

		public int colour;
		public int altColour;
		public String abbreviation;
		public int xBar;
		public int yBar;
		public Supplier<Boolean> enabled = () -> true;

		EnergySystem(int colour, String abbreviation, int xBar, int yBar, int altColour) {
			this.colour = colour;
			this.abbreviation = abbreviation;
			this.xBar = xBar;
			this.yBar = yBar;
			this.altColour = altColour;
		}
	}
}
