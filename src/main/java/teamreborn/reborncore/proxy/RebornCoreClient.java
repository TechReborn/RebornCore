package teamreborn.reborncore.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.reborninfoprovider.RebornInfoProviderHUD;

import java.io.File;

import static teamreborn.reborncore.RebornCore.configDir;

/**
 * Created by Prospector
 */
public class RebornCoreClient extends RebornCoreServer {

	public void preInit(FMLPreInitializationEvent event) {
		configDir = new File(event.getModConfigurationDirectory(), "teamreborn");
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		RebornInfoProviderHUD.ripConfig = (new File(configDir, "reborn_info_provider.json"));
		RebornInfoProviderHUD.reloadConfig();
	}

	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RebornInfoProviderHUD());
	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
