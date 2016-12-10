package reborncore.common.powerSystem;

import net.minecraft.client.Minecraft;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.tesla.TeslaManager;

import java.text.NumberFormat;
import java.util.Locale;

public class PowerSystem {
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

	public enum EnergySystem {
		TESLA(1421222, "Tesla", 71, 151),
		EU(8781824, "EU", 43, 151),
		FE(14831371, "FE", 15, 151);

		public int colour;
		public String abbreviation;
		public int xBar;
		public int yBar;

		EnergySystem(int colour, String abbreviation, int xBar, int yBar) {
			this.colour = colour;
			this.abbreviation = abbreviation;
			this.xBar = xBar;
			this.yBar = yBar;
		}
	}
}
