package reborncore;

import me.modmuss50.jsonDestroyer.JsonDestroyer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import reborncore.common.IModInfo;
import reborncore.common.LootManager;
import reborncore.common.RebornCoreConfig;
import reborncore.common.minetweaker.MinetweakerDocGen;
import reborncore.common.multiblock.MultiblockEventHandler;
import reborncore.common.multiblock.MultiblockServerTickHandler;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.RebornPackets;
import reborncore.common.packets.PacketHandler;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.tesla.TeslaManager;
import reborncore.common.util.*;
import reborncore.shields.RebornCoreShields;
import reborncore.shields.json.ShieldJsonLoader;

import java.io.File;

@Mod(modid = RebornCore.MOD_ID, name = RebornCore.MOD_NAME, version = RebornCore.MOD_VERSION, acceptedMinecraftVersions = "[1.11]", dependencies = "required-after:forge@[12.18.2.2121,);")
public class RebornCore implements IModInfo {

	public static final String MOD_NAME = "RebornCore";
	public static final String MOD_ID = "reborncore";
	public static final String MOD_VERSION = "@MODVERSION@";
	public static final String WEB_URL = "http://files.modmuss50.me/";

	public static LogHelper logHelper;
	public static JsonDestroyer jsonDestroyer = new JsonDestroyer();
	public static RebornCoreConfig config;
	@SidedProxy(clientSide = "reborncore.ClientProxy", serverSide = "reborncore.CommonProxy")
	public static CommonProxy proxy;
	public static File configDir;

	public RebornCore() {
		logHelper = new LogHelper(this);
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().registerCrashCallable(new CrashHandler());
		configDir = new File(event.getModConfigurationDirectory(), "teamreborn");
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		config = RebornCoreConfig.initialize(event.getSuggestedConfigurationFile());
		PowerSystem.priorityConfig = (new File(configDir, "energy_priority.json"));
		PowerSystem.reloadConfig();
		CalenderUtils.loadCalender(); //Done early as some features need this
		proxy.preInit(event);
		ShieldJsonLoader.load(event);
		if(RebornCoreConfig.mtDocGen && Loader.isModLoaded("MineTweaker3")){
			MinetweakerDocGen.gen(event.getAsmData(), new File(configDir, "MTDocs.txt"));
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		jsonDestroyer.load();
		TeslaManager.load();
		// packets
		PacketHandler.setChannels(NetworkRegistry.INSTANCE.newChannel(MOD_ID + "_packets", new PacketHandler()));
		OreUtil.scanForOres();
		MinecraftForge.EVENT_BUS.register(new RebornPackets());
		NetworkManager.load();

		RebornCoreShields.init();
		RebornPermissions.init();
		MinecraftForge.EVENT_BUS.register(LootManager.INSTANCE);
		//MinecraftForge.EVENT_BUS.register(InventoryCapabilityAttacher.instace);
		// Multiblock events
		MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
		MinecraftForge.EVENT_BUS.register(new MultiblockServerTickHandler());

		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	public String MOD_NAME() {
		return MOD_NAME;
	}

	@Override
	public String MOD_ID() {
		return MOD_ID;
	}

	@Override
	public String MOD_VERSION() {
		return MOD_VERSION;
	}

	@Override
	public String MOD_DEPENDENCIES() {
		return "";
	}
}
