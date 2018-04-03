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

/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */

package reborncore.client.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import reborncore.client.multiblock.component.MultiblockComponent;

import java.util.List;

public class MultiblockRenderEvent {

	public static BlockPos anchor;
	private static BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();
	public MultiblockSet currentMultiblock;
	//public Location parent;
	public BlockPos parent;
	RebornFluidRenderer fluidRenderer;

	public MultiblockRenderEvent() {
		this.fluidRenderer = new RebornFluidRenderer();
	}

	public void setMultiblock(MultiblockSet set) {
		currentMultiblock = set;
		anchor = null;
		parent = null;
	}

	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) throws Throwable {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player != null && mc.objectMouseOver != null && !mc.player.isSneaking()) {
			if (currentMultiblock != null) {
				BlockPos anchorPos = anchor != null ? anchor : mc.objectMouseOver.getBlockPos();

				Multiblock mb = currentMultiblock.getForIndex(0);

				for (MultiblockComponent comp : mb.getComponents()) {
					renderComponent(comp, anchorPos.up(), event.getPartialTicks(), mc.player);
				}

			}
		}
	}

	private void renderComponent(MultiblockComponent comp, BlockPos anchor, float partialTicks, EntityPlayerSP player) {
		double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		BlockPos pos = anchor.add(comp.getRelativePosition());
		Minecraft minecraft = Minecraft.getMinecraft();
		World world = player.world;

		minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		BlockRenderLayer originalLayer = MinecraftForgeClient.getRenderLayer();
		ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(-dx, -dy, -dz);
		GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());
		GlStateManager.scale(0.8, 0.8, 0.8);
		GlStateManager.translate(0.2, 0.2, 0.2);

		RenderHelper.disableStandardItemLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);
		int alpha = ((int) (0.5 * 0xFF)) << 24;
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		this.renderModel(world, pos, alpha, comp.state);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		ForgeHooksClient.setRenderLayer(originalLayer);
	}

	private void renderModel(World world, BlockPos pos, int alpha, IBlockState state) {
		if(state.getRenderType() == EnumBlockRenderType.LIQUID){
			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder buffer = tessellator.getBuffer();
			GlStateManager.translate(-pos.getX(), -pos.getY(), -pos.getZ());
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			fluidRenderer.renderFluid(world, state, pos, buffer);
			tessellator.draw();
			return;
		}
		IBakedModel model = blockRender.getModelForState(state);
		IBlockState extendedState = state.getBlock().getExtendedState(state, world, pos);
		for (final EnumFacing facing : EnumFacing.values()) {
			this.renderQuads(world, state, pos, model.getQuads(extendedState, facing, 0), alpha);
		}

		this.renderQuads(world, state, pos, model.getQuads(extendedState, null, 0), alpha);
	}

	private void renderQuads(final World world, final IBlockState actualState, final BlockPos pos, final List<BakedQuad> quads, final int alpha) {
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBuffer();

		if (quads == null || quads.isEmpty()) { //Bad things
			return;
		}
		for (final BakedQuad quad : quads) {
			buffer.begin(GL11.GL_QUADS, quad.getFormat());

			final int color = quad.hasTintIndex() ? this.getTint(world, actualState, pos, alpha, quad.getTintIndex()) : alpha | 0xffffff;
			LightUtil.renderQuadColor(buffer, quad, color);

			tessellator.draw();
		}
	}

	private int getTint(final World world, final IBlockState actualState, final BlockPos pos, final int alpha, final int tintIndex) {
		return alpha | Minecraft.getMinecraft().getBlockColors().colorMultiplier(actualState, world, pos, tintIndex);
	}

	@SubscribeEvent
	public void breakBlock(BlockEvent.BreakEvent event) {
		if (parent != null) {
			if (event.getPos().getX() == parent.getX() && event.getPos().getY() == parent.getY() && event.getPos().getZ() == parent.getZ()) {
				setMultiblock(null);
			}
		}
	}

	@SubscribeEvent
	public void worldUnloaded(WorldEvent.Unload event) {
		setMultiblock(null);
	}
}
