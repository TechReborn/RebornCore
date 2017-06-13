package reborncore.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import reborncore.mixin.implementations.forge.MixinForgeLoadingCore;

import java.awt.*;

/**
 * Created by modmuss50 on 18/01/2017.
 */
public class RenderEvent {

	@SubscribeEvent
	public static void render(TickEvent.RenderTickEvent event) {
		if (!MixinForgeLoadingCore.mixinsLoaded && !FMLForgePlugin.RUNTIME_DEOBF && Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen.getClass() == GuiMainMenu.class) {
			Minecraft.getMinecraft().fontRenderer.drawString("RebornCore Mixin Manager failed to load", 20, 5, Color.WHITE.getRGB());
			Minecraft.getMinecraft().fontRenderer.drawString("See MixinForgeLoadingCore for info on how to setup the vm options", 20, 15, Color.WHITE.getRGB());
		}
	}

}
