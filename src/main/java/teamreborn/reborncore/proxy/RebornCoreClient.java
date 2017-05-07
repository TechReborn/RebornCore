package teamreborn.reborncore.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.gui.assembler.JsonGuiAssembler;
import teamreborn.reborncore.manual.ManualData;
import teamreborn.reborncore.reborninfoprovider.RebornInfoProviderHUD;

import java.io.File;

import static teamreborn.reborncore.RebornCore.configDir;
import static teamreborn.reborncore.RebornCore.manualDir;

/**
 * Created by Prospector
 */
public class RebornCoreClient extends RebornCoreServer {

	public void preInit(FMLPreInitializationEvent event) {
		configDir = new File(event.getModConfigurationDirectory(), "teamreborn");
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		manualDir = new File(configDir, "manual");
		if (!manualDir.exists()) {
			manualDir.mkdir();
		}
		RebornInfoProviderHUD.ripConfig = new File(configDir, "reborn_info_provider.json");
		RebornInfoProviderHUD.reloadConfig();

		ManualData.manifest = new File(manualDir, "manifest.json");
		ManualData.reloadConfig();

		JsonGuiAssembler.defTextures.add(new JsonGuiAssembler.TextureElement(5, 8, 40, 20, JsonGuiAssembler.Layer.FOREGROUND, 0, 10));
		JsonGuiAssembler.defRects.add(new JsonGuiAssembler.RectangleElement(0, 0, 100, 100, JsonGuiAssembler.Layer.FOREGROUND, 0xFF414141));
		JsonGuiAssembler.defRectGradients.add(new JsonGuiAssembler.GradientElement(20, 60, 70, 4, JsonGuiAssembler.Layer.BACKGROUND, 0xFFFFFFFF, 0xFF000000));

		JsonGuiAssembler.json = new File(configDir, "guiConfig.json");
		JsonGuiAssembler.reloadConfig();

	}

	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RebornInfoProviderHUD());
	}

	public void postInit(FMLPostInitializationEvent event) {

	}
}
