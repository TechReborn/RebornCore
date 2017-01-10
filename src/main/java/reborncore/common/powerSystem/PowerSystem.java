package reborncore.common.powerSystem;

import net.minecraft.client.Minecraft;
import reborncore.RebornCore;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.tesla.TeslaManager;
import reborncore.mixin.json.JsonUtil;

import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;

public class PowerSystem
{
	public static File priorityConfig;
	private static int euPriority;
	private static int teslaPriority;
	private static int forgePriority;
	private static int euPriorityDefault = 0;
	private static int teslaPriorityDefault = 2;
	private static int forgePriorityDefault = 1;

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
		if (getDisplayPower().equals(EnergySystem.EU)) {
			return eu + " " + EnergySystem.EU.abbreviation;
		} else if (getDisplayPower().equals(EnergySystem.TESLA)) {
			return eu * RebornCoreConfig.euPerFU + " " + EnergySystem.TESLA.abbreviation;
		} else {
			return eu * RebornCoreConfig.euPerFU + " " + EnergySystem.FE.abbreviation;
		}
	}

	public static String getLocaliszedPowerFormatted(int eu) {
		if (getDisplayPower().equals(EnergySystem.EU)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu) + " " + EnergySystem.EU.abbreviation;
		} else if (getDisplayPower().equals(EnergySystem.TESLA)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerFU) + " " + EnergySystem.TESLA.abbreviation;
		} else {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerFU) + " " + EnergySystem.FE.abbreviation;
		}
	}

	public static String getLocaliszedPowerFormattedNoSuffix(int eu) {
		if (getDisplayPower().equals(EnergySystem.EU)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu);
		} else if (getDisplayPower().equals(EnergySystem.TESLA)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerFU);
		} else {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerFU);
		}
	}

	public static String getLocaliszedPowerNoSuffix(int eu) {
		if (getDisplayPower().equals(EnergySystem.EU)) {
			return eu + "";
		} else if (getDisplayPower().equals(EnergySystem.TESLA)) {
			return eu * RebornCoreConfig.euPerFU + "";
		} else {
			return eu * RebornCoreConfig.euPerFU + "";
		}
	}

	private static String getRoundedString(double euValue, String units) {
		if (euValue >= 1000000) {
			double tenX = Math.round(euValue / 100000);
			return Double.toString(tenX / 10.0).concat(" m " + units);
		} else if (euValue >= 1000) {
			double tenX = Math.round(euValue / 100);
			return Double.toString(tenX / 10.0).concat(" k " + units);
		} else {
			return Double.toString(Math.floor(euValue)).concat(" " + units);
		}
	}

	public static EnergySystem getDisplayPower() {
		int eu = RebornCoreConfig.euPriority;
		int tesla = RebornCoreConfig.teslaPriority;
		int fe = RebornCoreConfig.forgePriority;
		if ((eu > tesla || !TeslaManager.isTeslaEnabled(RebornCoreConfig.getRebornPower())) && eu > fe && RebornCoreConfig.getRebornPower().eu())
			return EnergySystem.EU;
		if ((tesla > eu || !RebornCoreConfig.getRebornPower().eu()) && tesla > fe && TeslaManager.isTeslaEnabled(RebornCoreConfig.getRebornPower()))
			return EnergySystem.TESLA;
		return EnergySystem.FE;
	}

	public static void bumpPowerConfig() {
		EnergyPriorityConfig config = new EnergyPriorityConfig();
		if (getDisplayPower() == EnergySystem.TESLA) {
			config.setEuPriority(2);
			config.setTeslaPriority(0);
			config.setForgePriority(1);
		} else if (getDisplayPower() == EnergySystem.EU) {
			config.setEuPriority(0);
			config.setTeslaPriority(1);
			config.setForgePriority(2);
		} else if (getDisplayPower() == EnergySystem.FE) {
			config.setEuPriority(1);
			config.setTeslaPriority(2);
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
				config = JsonUtil.GSON.fromJson(reader, EnergyPriorityConfig.class);
			} catch (Exception e) {
				e.printStackTrace();
				RebornCore.logHelper.error("Failed to read power config, will reset to defautls and save a new file.");
			}
			if(config == null){
				config = new EnergyPriorityConfig();
				writeConfig(config);
			}
			euPriority = config.euPriority;
			teslaPriority = config.teslaPriority;
			forgePriority = config.forgePriority;
		}
	}

	public static void writeConfig(EnergyPriorityConfig config) {
		try (Writer writer = new FileWriter(priorityConfig)) {
			JsonUtil.GSON.toJson(config, writer);
		} catch (Exception e) {

		}
		reloadConfig();
	}


	public enum EnergySystem {
		TESLA(0xFF1DBFB3, "Tesla", 71, 151, 0xFF09948C),
		EU(0xFF980000, "EU", 43, 151, 0xFF580000),
		FE(0xFFE14E1C, "FE", 15, 151, 0xFFB3380F);

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
		public int teslaPriority = teslaPriorityDefault;
		public int forgePriority = forgePriorityDefault;

		public int getEuPriority() {
			return euPriority;
		}

		public void setEuPriority(int euPriority) {
			this.euPriority = euPriority;
		}

		public int getTeslaPriority() {
			return teslaPriority;
		}

		public void setTeslaPriority(int teslaPriority) {
			this.teslaPriority = teslaPriority;
		}

		public int getForgePriority() {
			return forgePriority;
		}

		public void setForgePriority(int forgePriority) {
			this.forgePriority = forgePriority;
		}
	}

}
