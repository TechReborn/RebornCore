package teamreborn.reborncore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.concrete.NetworkContext;
import teamreborn.reborncore.init.RegistrationManager;
import teamreborn.reborncore.proxy.RebornCoreServer;

import java.io.File;

@Mod(name = "RebornCore", modid = Constants.MODID, version = "@MODVERSION@")
public class RebornCore {

	public static NetworkContext network;
	@SidedProxy(clientSide = "teamreborn.reborncore.proxy.RebornCoreClient", serverSide = "teamreborn.reborncore.proxy.RebornCoreServer")
	public static RebornCoreServer PROXY;
	public static File configDir;
	public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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
