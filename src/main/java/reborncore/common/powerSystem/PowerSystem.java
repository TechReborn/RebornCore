package reborncore.common.powerSystem;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Loader;
import reborncore.RebornCore;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.tesla.TeslaManager;
import reborncore.mixin.json.JsonUtil;

import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Predicate;

public class PowerSystem {
	public static File priorityConfig;
	public static EnergySystem energySystem = EnergySystem.FE;

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
		} else if (getDisplayPower().equals(EnergySystem.RF)) {
			return eu * RebornCoreConfig.euPerRF + " " + EnergySystem.RF.abbreviation;
		} else {
			return eu * RebornCoreConfig.euPerFU + " " + EnergySystem.FE.abbreviation;
		}
	}

	public static String getLocaliszedPowerFormatted(int eu) {
		if (getDisplayPower().equals(EnergySystem.EU)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu) + " " + EnergySystem.EU.abbreviation;
		} else if (getDisplayPower().equals(EnergySystem.TESLA)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerFU) + " " + EnergySystem.TESLA.abbreviation;
		} else if (getDisplayPower().equals(EnergySystem.RF)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerRF) + " " + EnergySystem.RF.abbreviation;
		} else {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerFU) + " " + EnergySystem.FE.abbreviation;
		}
	}

	public static String getLocaliszedPowerFormattedNoSuffix(int eu) {
		if (getDisplayPower().equals(EnergySystem.EU)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu);
		} else if (getDisplayPower().equals(EnergySystem.TESLA)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerFU);
		} else if (getDisplayPower().equals(EnergySystem.RF)) {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerRF);
		} else {
			return NumberFormat.getIntegerInstance(Locale.forLanguageTag(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())).format(eu * RebornCoreConfig.euPerFU);
		}
	}

	public static String getLocaliszedPowerNoSuffix(int eu) {
		if (getDisplayPower().equals(EnergySystem.EU)) {
			return eu + "";
		} else if (getDisplayPower().equals(EnergySystem.TESLA)) {
			return eu * RebornCoreConfig.euPerFU + "";
		} else if (getDisplayPower().equals(EnergySystem.RF)) {
			return eu * RebornCoreConfig.euPerRF + "";
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
		return energySystem;
	}

	public static void bumpPowerConfig() {
		int nextId = 0;
		if (energySystem.index > EnergySystem.values().length) {
			nextId = 0;
		} else {
			for (EnergySystem system : EnergySystem.values()) {
				if (system.index > energySystem.index && system.isValid.test(system)) {
					nextId = system.index;
					break;
				}
			}
		}
		energySystem = EnergySystem.indexOf(nextId);
		writeConfig(new EnergyPriorityConfig(energySystem));
	}

	public static void reloadConfig() {
		if (!priorityConfig.exists()) {
			writeConfig(new EnergyPriorityConfig(EnergySystem.FE));
			energySystem = EnergySystem.FE;
		}
		if (priorityConfig.exists()) {
			EnergyPriorityConfig config = null;
			try (Reader reader = new FileReader(priorityConfig)) {
				config = JsonUtil.GSON.fromJson(reader, EnergyPriorityConfig.class);
			} catch (Exception e) {
				e.printStackTrace();
				RebornCore.logHelper.error("Failed to read power config, will reset to defautls and save a new file.");
				priorityConfig.delete();
				config = new EnergyPriorityConfig(EnergySystem.FE);
				writeConfig(config);
			}
			if (config == null) {
				config = new EnergyPriorityConfig(EnergySystem.FE);
				writeConfig(config);
			}
			energySystem = config.energySystem;
		}
	}

	public static void writeConfig(EnergyPriorityConfig config) {
		try (Writer writer = new FileWriter(priorityConfig)) {
			JsonUtil.GSON.toJson(config, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum EnergySystem {
		TESLA(0xFF1DBFB3, "Tesla", 71, 151, 0xFF09948C, 0, system -> TeslaManager.isTeslaEnabled(RebornCoreConfig.getRebornPower())),
		EU(0xFF980000, "EU", 43, 151, 0xFF580000, 1, system -> RebornCoreConfig.getRebornPower().eu() && Loader.isModLoaded("IC2")),
		FE(0xFFE14E1C, "FE", 15, 151, 0xFFB3380F, 2, system -> RebornCoreConfig.getRebornPower().forge()),
		RF(0xFFE14E1C, "RF", 15, 151, 0xFFB3380F, 3, system -> RebornCoreConfig.getRebornPower().rf());

		public int colour;
		public int altColour;
		public String abbreviation;
		public int xBar;
		public int yBar;
		private int index;
		private Predicate<EnergySystem> isValid;

		EnergySystem(int colour, String abbreviation, int xBar, int yBar, int altColour, int index, Predicate<EnergySystem> isValid) {
			this.colour = colour;
			this.abbreviation = abbreviation;
			this.xBar = xBar;
			this.yBar = yBar;
			this.altColour = altColour;
			this.index = index;
			this.isValid = isValid;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public static EnergySystem indexOf(int index) {
			for (EnergySystem system : EnergySystem.values()) {
				if (system.index == index) {
					return system;
				}
			}
			return FE;
		}
	}

	public static class EnergyPriorityConfig {
		public EnergySystem energySystem;

		public EnergyPriorityConfig(EnergySystem energySystem) {
			this.energySystem = energySystem;
		}
	}

}
