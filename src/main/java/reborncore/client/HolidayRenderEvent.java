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

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

/**
 * Created by Mark on 27/11/2016.
 */
public class HolidayRenderEvent {

	static ModelSantaHat santaHat = new ModelSantaHat();
	private static final Identifier TEXTURE = new Identifier("reborncore", "textures/models/santa_hat.png");

	public static class LayerRender extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

		public LayerRender(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext_1) {
			super(featureRendererContext_1);
		}

		@Override
		public void render(AbstractClientPlayerEntity abstractClientPlayer,
		                   float limbSwing,
		                   float limbSwingAmount,
		                   float partialTicks,
		                   float ageInTicks,
		                   float netHeadYaw,
		                   float headPitch,
		                   float scale) {
			float yaw = abstractClientPlayer.prevYaw + (abstractClientPlayer.yaw - abstractClientPlayer.prevYaw) * partialTicks - (abstractClientPlayer.field_6220 + (abstractClientPlayer.field_6283 - abstractClientPlayer.field_6220) * partialTicks);
			float pitch = abstractClientPlayer.prevPitch + (abstractClientPlayer.pitch - abstractClientPlayer.prevPitch) * partialTicks;
			MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
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
		public boolean hasHurtOverlay() {
			return false;
		}

	}

}
