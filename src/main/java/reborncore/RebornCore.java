/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import reborncore.api.ToolManager;
import reborncore.client.gui.ManualGuiHandler;
import reborncore.common.IModInfo;
import reborncore.common.LootManager;
import reborncore.common.RebornCoreConfig;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.blocks.BlockWrenchEventHandler;
import reborncore.common.commands.CommandListMods;
import reborncore.common.commands.CommandListRecipes;
import reborncore.common.logic.LogicControllerGuiHandler;
import reborncore.common.logic.PacketButtonID;
import reborncore.common.multiblock.MultiblockEventHandler;
import reborncore.common.multiblock.MultiblockServerTickHandler;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.RegisterPacketEvent;
import reborncore.common.network.packet.*;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryConstructionEvent;
import reborncore.common.registration.impl.ConfigRegistryFactory;
import reborncore.common.scriba.TileRegistrationManager;
import reborncore.common.util.*;
import reborncore.modcl.manual.ItemTeamRebornManual;
import reborncore.shields.RebornCoreShields;
import reborncore.shields.json.ShieldJsonLoader;

import java.io.File;

@Mod(modid = RebornCore.MOD_ID, name = RebornCore.MOD_NAME, version = RebornCore.MOD_VERSION, acceptedMinecraftVersions = "[1.12]", dependencies = "required-after:forge@[14.21.0.2359,);", certificateFingerprint = "8727a3141c8ec7f173b87aa78b9b9807867c4e6b")
public class RebornCore implements IModInfo {

	public static final String MOD_NAME = "Reborn Core";
	public static final String MOD_ID = "reborncore";
	public static final String MOD_VERSION = "@MODVERSION@";
	public static final String WEB_URL = "https://files.modmuss50.me/";

	public static LogHelper logHelper;
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
		MinecraftForge.EVENT_BUS.register(ConfigRegistryFactory.class);
		ConfigRegistryFactory.setConfigDir(configDir);
		RegistrationManager.init(event);
		RegistrationManager.load(new RegistryConstructionEvent());
		ConfigRegistryFactory.saveAll();
		MinecraftForge.EVENT_BUS.register(OreRegistationEvent.class);
		PowerSystem.selectedFile = (new File(configDir, "reborncore/selected_energy.json"));
		PowerSystem.readFile();
		CalenderUtils.loadCalender(); //Done early as some features need this
		proxy.preInit(event);
		ShieldJsonLoader.load(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new LogicControllerGuiHandler());
		MinecraftForge.EVENT_BUS.register(this);

		RegistrationManager.load(event);

		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("ic2:wrench"), true));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("forestry:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("actuallyadditions:item_laser_wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("thermalfoundation:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("charset:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("teslacorelib:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("rftools:smartwrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("intergrateddynamics:smartwrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("correlated:weldthrower"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("chiselsandbits:wrench_wood"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new ResourceLocation("redstonearsenal:tool.wrench_flux"), false));

		PowerSystem.EnergySystem.FE.enabled = () -> RebornCoreConfig.enableFE;

		TileRegistrationManager.INSTANCE.registerReferencedTiles();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		// packets
		OreUtil.scanForOres();
		NetworkManager.load();

		RebornCoreShields.init();
		RebornPermissions.init();
		MinecraftForge.EVENT_BUS.register(LootManager.INSTANCE);
		//MinecraftForge.EVENT_BUS.register(InventoryCapabilityAttacher.instace);
		// Multiblock events
		MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
		MinecraftForge.EVENT_BUS.register(new MultiblockServerTickHandler());
		MinecraftForge.EVENT_BUS.register(BlockWrenchEventHandler.class);
		MinecraftForge.EVENT_BUS.register(BlockMachineBase.class);

		if (ItemTeamRebornManual.isManualEnabled) {
			RebornRegistry.registerItem(new ItemTeamRebornManual());
			NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new ManualGuiHandler());
		}

		proxy.init(event);
		RegistrationManager.load(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
		RegistrationManager.load(event);
		try {
			OreUtil.remove("blockMetal");
		} catch (Exception e) {
			RebornCore.logHelper.error("Failed to remove ore");
			RebornCore.logHelper.error(e);
		}
	}

	@Mod.EventHandler
	public void loaded(FMLLoadCompleteEvent event){
		OreRegistationEvent.loadComplete();
	}

	@Mod.EventHandler
	public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		logHelper.error("Invalid fingerprint detected for RebornCore!");
		RebornCore.proxy.invalidFingerprints.add("Invalid fingerprint detected for RebornCore!");
	}

	@SubscribeEvent
	public void registerPackets(RegisterPacketEvent event){
		event.registerPacket(PacketButtonID.class, Side.SERVER);
		event.registerPacket(CustomDescriptionPacket.class, Side.CLIENT);
		event.registerPacket(PacketSlotSave.class, Side.SERVER);
		event.registerPacket(PacketFluidConfigSave.class, Side.SERVER);
		event.registerPacket(PacketConfigSave.class, Side.SERVER);
		event.registerPacket(PacketSlotSync.class, Side.CLIENT);
		event.registerPacket(PacketFluidConfigSync.class, Side.CLIENT);
		event.registerPacket(PacketIOSave.class, Side.SERVER);
		event.registerPacket(PacketFluidIOSave.class, Side.SERVER);
		event.registerPacket(PacketSendLong.class, Side.CLIENT);
		event.registerPacket(PacketSendObject.class, Side.CLIENT);
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandListRecipes());
		event.registerServerCommand(new CommandListMods());
	}

	@Override
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
