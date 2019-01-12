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

package reborncore.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import reborncore.client.models.ModelSantaHat;
import reborncore.common.RebornCoreConfig;
import reborncore.common.util.CalenderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 27/11/2016.
 */
public class HolidayRenderEvent {

	static ModelSantaHat santaHat = new ModelSantaHat();
	private static final ResourceLocation TEXTURE = new ResourceLocation("reborncore", "textures/models/santa_hat.png");
	static List<RenderPlayer> renderPlayerList = new ArrayList<>();

	@SubscribeEvent
	public static void holidayRender(RenderPlayerEvent.Pre event) {

		if (!CalenderUtils.christmas || !RebornCoreConfig.easterEggs) {
			return;
		}
		Render<?> render = Minecraft.getInstance().getRenderManager().getEntityRenderObject(event.getEntityPlayer());
		if (render instanceof RenderPlayer) {
			RenderPlayer renderPlayer = (RenderPlayer) render;
			if (!renderPlayerList.contains(renderPlayer)) {
				renderPlayer.addLayer(new LayerRender());
				renderPlayerList.add(renderPlayer);
			}
		}

	}

	private static class LayerRender implements LayerRenderer<AbstractClientPlayer> {

		@Override
		public void render(AbstractClientPlayer abstractClientPlayer,
		                          float limbSwing,
		                          float limbSwingAmount,
		                          float partialTicks,
		                          float ageInTicks,
		                          float netHeadYaw,
		                          float headPitch,
		                          float scale) {
			float yaw = abstractClientPlayer.prevRotationYaw + (abstractClientPlayer.rotationYaw - abstractClientPlayer.prevRotationYaw) * partialTicks - (abstractClientPlayer.prevRenderYawOffset + (abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset) * partialTicks);
			float pitch = abstractClientPlayer.prevRotationPitch + (abstractClientPlayer.rotationPitch - abstractClientPlayer.prevRotationPitch) * partialTicks;
			Minecraft.getInstance().textureManager.bindTexture(TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(pitch, 1.0F, 0.0F, 0.0F);
			//GlStateManager.translate(-0.25F, -0.0F, 0.0F);
			if (abstractClientPlayer.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.26F, 0.0F);
			}

			float scale2 = 1.0F;
			GlStateManager.scalef(scale2, scale2, scale2);
			santaHat.render(0.0625F);
			GlStateManager.popMatrix();

		}

		@Override
		public boolean shouldCombineTextures() {
			return true;
		}
	}

}
