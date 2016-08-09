package reborncore.common.powerSystem;

import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.tesla.TeslaManager;

public class PowerSystem
{
	public static String getLocaliszedPower(double eu)
	{
		return getLocaliszedPower((int) eu);
	}

	public static String getLocaliszedPower(int eu)
	{
		if (RebornCoreConfig.getRebornPower().eu())
		{
			return getRoundedString(eu, "EU");
		} else if (TeslaManager.isTeslaEnabled(RebornCoreConfig.getRebornPower()))
		{
			return TeslaManager.manager.getDisplayableTeslaCount(eu);
		}else
		{
			return getRoundedString(eu / RebornCoreConfig.euPerRF, "RF");
		}
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
