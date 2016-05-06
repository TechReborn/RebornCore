package reborncore.common.powerSystem.tesla;

import net.minecraftforge.fml.common.Loader;
import reborncore.common.RebornCoreConfig;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class TeslaManager {

    public static ITeslaPowerManager manager;

    private static boolean isTeslaEnabled = false;

    public static void load(){
        isTeslaEnabled = Loader.isModLoaded("Tesla");
        if(isTeslaEnabled){
            manager = TeslaPowerManager.getPowerManager();
        }
    }

    public static boolean isTeslaEnabled(){
        return isTeslaEnabled && RebornCoreConfig.getRebornPower().tesla() && manager != null;
    }

}
