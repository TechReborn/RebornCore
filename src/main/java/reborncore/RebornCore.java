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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.metadata.LoaderModMetadata;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reborncore.api.ToolManager;
import reborncore.common.RebornCoreConfig;
import reborncore.common.blocks.BlockWrenchEventHandler;
import reborncore.common.multiblock.MultiblockEventHandler;
import reborncore.common.multiblock.MultiblockServerTickHandler;
import reborncore.common.network.ClientBoundPackets;
import reborncore.common.network.ServerBoundPackets;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.shields.RebornCoreShields;
import reborncore.common.shields.json.ShieldJsonLoader;
import reborncore.common.util.CalenderUtils;
import reborncore.common.util.GenericWrenchHelper;
import java.io.File;

public class RebornCore implements ModInitializer {

	public static final String MOD_NAME = "Reborn Core";
	public static final String MOD_ID = "reborncore";
	public static final String MOD_VERSION = "@MODVERSION@";
	public static final String WEB_URL = "https://files.modmuss50.me/";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static CommonProxy proxy = new ClientProxy();
	public static File configDir;

	public static boolean LOADED = false;

	public RebornCore() {

	}
	@Override
	public void onInitialize() {
		LOGGER.info("Hello minecraft!");

		//TODO this may explode, find a better way to get config dir :D
		configDir = new File(new File("config"), "teamreborn");
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		//MinecraftForge.EVENT_BUS.register(ConfigRegistryFactory.class);
		//ConfigRegistryFactory.setConfigDir(configDir);
		RegistrationManager registrationManager = new RegistrationManager("reborncore");
		//ConfigRegistryFactory.saveAll();
		PowerSystem.selectedFile = (new File(configDir, "reborncore/selected_energy.json"));
		PowerSystem.readFile();
		CalenderUtils.loadCalender(); //Done early as some features need this
		proxy.setup();
		ShieldJsonLoader.load();

		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("ic2:wrench"), true));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("forestry:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("actuallyadditions:item_laser_wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("thermalfoundation:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("charset:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("teslacorelib:wrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("rftools:smartwrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("intergrateddynamics:smartwrench"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("correlated:weldthrower"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("chiselsandbits:wrench_wood"), false));
		ToolManager.INSTANCE.customToolHandlerList.add(new GenericWrenchHelper(new Identifier("redstonearsenal:tool.wrench_flux"), false));

		PowerSystem.EnergySystem.FE.enabled = () -> RebornCoreConfig.enableFE;

		RebornCoreShields.init();

		// Multiblock events
		MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
		MinecraftForge.EVENT_BUS.register(new MultiblockServerTickHandler());
		MinecraftForge.EVENT_BUS.register(BlockWrenchEventHandler.class);

		// packets
		ServerBoundPackets.init();
		ClientBoundPackets.init();
		LOGGER.info("Reborn core is done for now, now to let other mods have their turn...");
		LOADED = true;
	}

	public static EnvType getSide() {
		return FabricLoader.getInstance().getEnvironmentType();
	}

}
