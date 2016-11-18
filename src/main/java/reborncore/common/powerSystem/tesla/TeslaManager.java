package reborncore.common.powerSystem.tesla;

import net.minecraftforge.fml.common.Loader;
import reborncore.api.power.IPowerConfig;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class TeslaManager {

	public static ITeslaPowerManager manager;

	private static boolean isTeslaEnabled = false;

	public static void load() {
		isTeslaEnabled = Loader.isModLoaded("tesla");
		if (isTeslaEnabled) {
			manager = TeslaPowerManager.getPowerManager();
		}
	}

	public static boolean isTeslaEnabled(IPowerConfig config) {
		return isTeslaEnabled && config.tesla() && manager != null;
	}

}
