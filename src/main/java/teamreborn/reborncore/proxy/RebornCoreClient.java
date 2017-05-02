package teamreborn.reborncore.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.reborninfoprovider.RebornInfoProviderHUD;
import teamreborn.reborncore.reborninfoprovider.elements.BlockDisplayElement;
import teamreborn.reborncore.reborninfoprovider.elements.StringDisplayElement;
import teamreborn.reborncore.reborninfoprovider.elements.WeatherDisplayElement;

/**
 * Created by Prospector
 */
public class RebornCoreClient extends RebornCoreServer {

	public void preInit(FMLPreInitializationEvent event) {

	}

	public void init(FMLInitializationEvent event) {
		RebornInfoProviderHUD.addElement(new StringDisplayElement("This is a RIP element! woohoo!"));
		RebornInfoProviderHUD.addElement(new WeatherDisplayElement());
		RebornInfoProviderHUD.addElement(new BlockDisplayElement());
		MinecraftForge.EVENT_BUS.register(new RebornInfoProviderHUD());
	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
