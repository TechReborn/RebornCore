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
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import reborncore.RebornCoreClient;
import reborncore.common.fluid.container.FluidInstance;
import reborncore.common.util.Tank;
import net.minecraft.fluid.Fluid;

/**
 * Created by Gigabit101 on 08/08/2016.
 */
public class RenderUtil {
	public static final Identifier BLOCK_TEX = SpriteAtlasTexture.BLOCK_ATLAS_TEX;

	public static TextureManager engine() {
		return MinecraftClient.getInstance().getTextureManager();
	}

	public static void bindBlockTexture() {
		engine().bindTexture(BLOCK_TEX);
	}

	public static Sprite getStillTexture(FluidInstance fluid) {
		if (fluid == null || fluid.getFluid() == null) {
			return null;
		}
		return getStillTexture(fluid.getFluid());
	}

	public static Sprite getStillTexture(Fluid fluid) {
		return RebornCoreClient.hooks.getFluidSprite(fluid, MinecraftClient.getInstance().world, BlockPos.ORIGIN);
	}

	public static void renderGuiTank(Tank tank, double x, double y, double zLevel, double width, double height) {
		renderGuiTank(tank.getFluidInstance(), tank.getCapacity(), tank.getFluidAmount(), x, y, zLevel, width, height);
	}

	public static void renderGuiTank(FluidInstance fluid, int capacity, int amount, double x, double y, double zLevel,
	                                 double width, double height) {
		if (fluid == null || fluid.getFluid() == null || fluid.getAmount() <= 0) {
			return;
		}

		Sprite icon = getStillTexture(fluid);
		if (icon == null) {
			return;
		}

		int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1);
		int posY = (int) (y + height - renderAmount);

		RenderUtil.bindBlockTexture();
		int color = 0;
		GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));

		GlStateManager.enableBlend();
		for (int i = 0; i < width; i += 16) {
			for (int j = 0; j < renderAmount; j += 16) {
				int drawWidth = (int) Math.min(width - i, 16);
				int drawHeight = Math.min(renderAmount - j, 16);

				int drawX = (int) (x + i);
				int drawY = posY + j;

				double minU = icon.getMinU();
				double maxU = icon.getMaxU();
				double minV = icon.getMinV();
				double maxV = icon.getMaxV();

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder tes = tessellator.getBufferBuilder();
				tes.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
				tes.vertex(drawX, drawY + drawHeight, 0).texture(minU, minV + (maxV - minV) * drawHeight / 16F).next();
				tes.vertex(drawX + drawWidth, drawY + drawHeight, 0)
					.texture(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F)
					.next();
				tes.vertex(drawX + drawWidth, drawY, 0).texture(minU + (maxU - minU) * drawWidth / 16F, minV).next();
				tes.vertex(drawX, drawY, 0).texture(minU, minV).next();
				tessellator.draw();
			}
		}
		GlStateManager.disableBlend();
	}

	public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBufferBuilder();
		vertexbuffer.begin(7, VertexFormats.POSITION_COLOR);
		vertexbuffer.vertex((double) right, (double) top, (double) 0).color(f1, f2, f3, f).next();
		vertexbuffer.vertex((double) left, (double) top, (double) 0).color(f1, f2, f3, f).next();
		vertexbuffer.vertex((double) left, (double) bottom, (double) 0).color(f5, f6, f7, f4).next();
		vertexbuffer.vertex((double) right, (double) bottom, (double) 0).color(f5, f6, f7, f4).next();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

}
