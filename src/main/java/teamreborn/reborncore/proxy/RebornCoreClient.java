package teamreborn.reborncore.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.reborninfoprovider.RebornInfoProviderHUD;

/**
 * Created by Prospector
 */
public class RebornCoreClient extends RebornCoreServer {

	public void preInit(FMLPreInitializationEvent event) {

	}

	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RebornInfoProviderHUD());
	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
