package reborncore.common.powerSystem;

import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.tesla.TeslaManager;

public class PowerSystem {
	public static String getLocaliszedPower(double eu) {
		return getLocaliszedPower((int) eu);
	}

	public static String getLocaliszedPower(int eu) {
		if (RebornCoreConfig.getRebornPower().eu()) {
			return getRoundedString(eu, "EU");
		} else if (TeslaManager.isTeslaEnabled(RebornCoreConfig.getRebornPower())) {
			return TeslaManager.manager.getDisplayableTeslaCount(eu);
		} else {
			return getRoundedString(eu * RebornCoreConfig.euPerFU, "FU");
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
		if (eu > tesla && eu > fe && RebornCoreConfig.getRebornPower().eu())
			return EnergySystem.EU;
		if (tesla > eu && tesla > fe && TeslaManager.isTeslaEnabled(RebornCoreConfig.getRebornPower()))
			return EnergySystem.TESLA;
		return EnergySystem.FE;
	}

	public enum EnergySystem {
		TESLA(1421222, "Tesla"),
		EU(11534340, "EU"),
		FE(14831371, "FE");

		public int colour;
		public String abbreviation;

		EnergySystem(int colour, String abbreviation) {
			this.colour = colour;
			this.abbreviation = abbreviation;
		}
	}
}
