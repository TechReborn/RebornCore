package teamreborn.reborncore;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.concrete.NetworkContext;
import teamreborn.reborncore.init.RegistrationManager;
import teamreborn.reborncore.proxy.RebornCoreServer;
import teamreborn.reborncore.rip.RIPHud;
import teamreborn.techreborn.TRConstants;
import teamreborn.techreborn.proxy.TechRebornServer;

@Mod(name = "RebornCore", modid = Constants.MODID, version = "@MODVERSION@")
public class RebornCore {

	public static NetworkContext network;
	@SidedProxy(clientSide = "teamreborn.reborncore.proxy.RebornCoreClient", serverSide = "teamreborn.reborncore.proxy.RebornCoreServer")
	public static RebornCoreServer PROXY;

	public RebornCore() {
		FluidRegistry.enableUniversalBucket();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		PROXY.preInit(event);
		network = NetworkContext.forChannel(Constants.MODID);

		RegistrationManager.load(event);

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		PROXY.init(event);
		RegistrationManager.handle(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		PROXY.postInit(event);
		RegistrationManager.handle(event);
	}

}
