package reborncore.common.util;

import net.minecraftforge.fml.common.Loader;
import reborncore.common.RebornCoreConfig;

public class PowerLocalization
{
	public static String getLocalizedPower(double eu) {
		return getLocalizedPower((int) eu);
	}

	public static String getLocalizedPower(int eu) {
		if (RebornCoreConfig.getRebornPower().tesla() && Loader.isModLoaded("Tesla")) {
			return getRoundedString(eu * RebornCoreConfig.euPerRF, "T");
		} else if(RebornCoreConfig.getRebornPower().eu()) {
			return getRoundedString(eu, "EU");
		}
		return getRoundedString(eu * RebornCoreConfig.euPerRF, "RF");
	}

	private static String getRoundedString(double euValue, String units)
	{
		if (euValue >= 1000000)
		{
			double tenX = Math.round(euValue / 100000);
			return Double.toString(tenX / 10.0).concat(" m " + units);
		} else if (euValue >= 1000)
		{
			double tenX = Math.round(euValue / 100);
			return Double.toString(tenX / 10.0).concat(" k " + units);
		} else
		{
			return Double.toString(Math.floor(euValue)).concat(" " + units);
		}
	}

}
