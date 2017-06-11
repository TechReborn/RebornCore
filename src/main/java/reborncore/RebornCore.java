package reborncore;

import me.modmuss50.jsonDestroyer.JsonDestroyer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import reborncore.client.gui.ManualGuiHandler;
import reborncore.common.IModInfo;
import reborncore.common.LootManager;
import reborncore.common.RebornCoreConfig;
import reborncore.common.logic.LogicControllerGuiHandler;
import reborncore.common.minetweaker.MinetweakerDocGen;
import reborncore.common.multiblock.MultiblockEventHandler;
import reborncore.common.multiblock.MultiblockServerTickHandler;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.RebornPackets;
import reborncore.common.packets.PacketHandler;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.tesla.TeslaManager;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryConstructionEvent;
import reborncore.common.registration.impl.ConfigRegistryFactory;
import reborncore.common.util.*;
import reborncore.modcl.manual.ItemTeamRebornManual;
import reborncore.shields.RebornCoreShields;
import reborncore.shields.json.ShieldJsonLoader;

import java.io.File;

@Mod(modid = RebornCore.MOD_ID, name = RebornCore.MOD_NAME, version = RebornCore.MOD_VERSION, acceptedMinecraftVersions = "[1.12]", dependencies = "required-after:forge@[12.18.2.2121,);", certificateFingerprint = "8727a3141c8ec7f173b87aa78b9b9807867c4e6b")
public class RebornCore implements IModInfo {

	public static final String MOD_NAME = "Reborn Core";
	public static final String MOD_ID = "reborncore";
	public static final String MOD_VERSION = "@MODVERSION@";
	public static final String WEB_URL = "http://files.modmuss50.me/";

	public static LogHelper logHelper;
	@Deprecated
	public static JsonDestroyer jsonDestroyer = new JsonDestroyer();
	public static RebornCoreConfig config;
	@Mod.Instance
	public static RebornCore INSTANCE;
	@SidedProxy(clientSide = "reborncore.ClientProxy", serverSide = "reborncore.CommonProxy")
	public static CommonProxy proxy;
	public static File configDir;

	public RebornCore() {
		logHelper = new LogHelper(this);
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		INSTANCE = this;
		FMLCommonHandler.instance().registerCrashCallable(new CrashHandler());
		configDir = new File(event.getModConfigurationDirectory(), "teamreborn");
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		ConfigRegistryFactory.setConfigDir(configDir);
		RegistrationManager.init(event);
		RegistrationManager.load(new RegistryConstructionEvent());
		ConfigRegistryFactory.saveAll();
		config = RebornCoreConfig.initialize(event.getSuggestedConfigurationFile());
		PowerSystem.priorityConfig = (new File(configDir, "energy_priority.json"));
		PowerSystem.reloadConfig();
		CalenderUtils.loadCalender(); //Done early as some features need this
		proxy.preInit(event);
		ShieldJsonLoader.load(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new LogicControllerGuiHandler());

		if (RebornCoreConfig.mtDocGen && Loader.isModLoaded("crafttweaker")) {
			MinetweakerDocGen.gen(event.getAsmData(), new File(configDir, "MTDocs.txt"));
		}
		RegistrationManager.load(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		RebornCoreConfig.isIC2Loaded = Loader.isModLoaded("ic2");
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

		if (ItemTeamRebornManual.isManualEnabled) {
			GameRegistry.register(new ItemTeamRebornManual());
			NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new ManualGuiHandler());
		}

		proxy.init(event);
		RegistrationManager.load(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
		RegistrationManager.load(event);
	}

	@Mod.EventHandler
	public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		FMLLog.warning("Invalid fingerprint detected for RebornCore!");
		RebornCore.proxy.invalidFingerprints.add("Invalid fingerprint detected for RebornCore!");
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
