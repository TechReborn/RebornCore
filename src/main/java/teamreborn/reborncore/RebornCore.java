package teamreborn.reborncore;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.concrete.NetworkContext;
import teamreborn.reborncore.init.RegistrationManager;

@Mod (name = "RebornCore", modid = Constants.MODID, version = "@MODVERSION@")
public class RebornCore
{

	public static NetworkContext network;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		network = NetworkContext.forChannel(Constants.MODID);

		RegistrationManager.load(event);

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		RegistrationManager.handle(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		RegistrationManager.handle(event);
	}

}
