/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
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
