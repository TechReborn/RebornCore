package teamreborn.reborncore;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.concrete.NetworkContext;
import teamreborn.reborncore.container.sync.ContainerUpdatePacket;
import teamreborn.reborncore.init.RegistrationManager;

@Mod (name = "RebornCore", modid = "reborncore", version = "@MODVERSION@")
public class RebornCore
{

	public static NetworkContext network;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		network = NetworkContext.forChannel("reborncore");
		network.register(ContainerUpdatePacket.class);

		RegistrationManager.load(event);
	}

}
