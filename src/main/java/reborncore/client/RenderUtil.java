package reborncore.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Created by Gigabit101 on 08/08/2016.
 */
public class RenderUtil {
	public static final ResourceLocation BLOCK_TEX = TextureMap.LOCATION_BLOCKS_TEXTURE;

	public static TextureManager engine() {
		return Minecraft.getMinecraft().renderEngine;
	}

	public static void bindBlockTexture() {
		engine().bindTexture(BLOCK_TEX);
	}

	public static TextureAtlasSprite getStillTexture(FluidStack fluid) {
		if (fluid == null || fluid.getFluid() == null) {
			return null;
		}
		return getStillTexture(fluid.getFluid());
	}

	public static TextureAtlasSprite getStillTexture(Fluid fluid) {
		ResourceLocation iconKey = fluid.getStill();
		if (iconKey == null) {
			return null;
		}
		return Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(iconKey.toString());
	}

	public static void renderGuiTank(FluidTank tank, double x, double y, double zLevel, double width, double height) {
		renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, zLevel, width, height);
	}

	public static void renderGuiTank(FluidStack fluid, int capacity, int amount, double x, double y, double zLevel,
	                                 double width, double height) {
		if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
			return;
		}

		TextureAtlasSprite icon = getStillTexture(fluid);
		if (icon == null) {
			return;
		}

		int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1);
		int posY = (int) (y + height - renderAmount);

		RenderUtil.bindBlockTexture();
		int color = fluid.getFluid().getColor(fluid);
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
				BufferBuilder tes = tessellator.getBuffer();
				tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				tes.pos(drawX, drawY + drawHeight, 0).tex(minU, minV + (maxV - minV) * drawHeight / 16F).endVertex();
				tes.pos(drawX + drawWidth, drawY + drawHeight, 0)
					.tex(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F)
					.endVertex();
				tes.pos(drawX + drawWidth, drawY, 0).tex(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
				tes.pos(drawX, drawY, 0).tex(minU, minV).endVertex();
				tessellator.draw();
			}
		}
		GlStateManager.disableBlend();
	}

	//Todo use for tinker table
	@SideOnly(Side.CLIENT)
	public static void renderBlockInWorld(Block block, int meta) {
		renderItemInWorld(new ItemStack(block, 1, meta));
	}

	@SideOnly(Side.CLIENT)
	public static void renderItemInWorld(ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();

		GlStateManager.pushMatrix();

		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		if (stack.getItem() instanceof ItemBlock) {
			GlStateManager.scale(.6, .6, .6);
		} else {
			GlStateManager.scale(.5, .5, .5);
		}

		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

	public static void drawBar(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos((double) (x), (double) (y), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + width), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + width), (double) (y), 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}
}
