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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import reborncore.client.multiblock.component.MultiblockComponent;
import reborncore.mixin.extensions.CameraExtensions;
import reborncore.mixin.extensions.FluidBlockExtensions;

import java.util.Random;

public class MultiblockRenderEvent implements AttackBlockCallback {

	public static BlockPos anchor;
	public MultiblockSet currentMultiblock;
	public BlockPos parent;

	public void setMultiblock(MultiblockSet set) {
		currentMultiblock = set;
		anchor = null;
		parent = null;
	}

	public void onWorldRenderLast(float partialTicks) {
		MinecraftClient mc = MinecraftClient.getInstance();

		CameraExtensions cameraExtensions = (CameraExtensions) MinecraftClient.getInstance().gameRenderer.getCamera();
		float sneak = cameraExtensions.getCameraY();

		if (mc.player != null && anchor != null) {
			if (currentMultiblock != null && sneak > 1.618F) {
				Multiblock mb = currentMultiblock.getForIndex(0);
				for (MultiblockComponent comp : mb.getComponents()) {
					renderComponent(comp, anchor.up(), partialTicks, mc.player);
				}
			}
		}
	}

	private void renderComponent(MultiblockComponent comp, BlockPos anchor, float partialTicks, ClientPlayerEntity player) {

		double dx = player.prevRenderX + (player.getX() - player.prevRenderX) * partialTicks;
		double dy = player.prevRenderY + (player.getY() - player.prevRenderY) * partialTicks;
		double dz = player.prevRenderZ + (player.getZ() - player.prevRenderZ) * partialTicks;

		BlockPos pos = anchor.add(comp.getRelativePosition());
		MinecraftClient minecraft = MinecraftClient.getInstance();
		World world = player.world;

		minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);

		RenderSystem.pushMatrix();
		RenderSystem.translated(-dx, -dy, -dz);
		RenderSystem.translated(pos.getX(), pos.getY() -1.7, pos.getZ());
		RenderSystem.scaled(0.8, 0.8, 0.8);
		RenderSystem.translated(0.2, 0.2, 0.2);

		GuiLighting.disable();
		RenderSystem.enableBlend();

		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.CONSTANT_ALPHA);
		GL14.glBlendColor(1F, 1F, 1F, 0.35F);

		this.renderModel(world, pos, comp.state);

		RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
	}

	private void renderModel(World world, BlockPos pos, BlockState state) {
		final BlockRenderManager blockRendererDispatcher = MinecraftClient.getInstance().getBlockRenderManager();
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBuffer();
		RenderSystem.translated(-pos.getX(), -pos.getY(), -pos.getZ());
		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

		if(state.getBlock() instanceof FluidBlock){
			FluidBlockExtensions fluidBlockExtensions = (FluidBlockExtensions) state.getBlock();
			blockRendererDispatcher.renderFluid(pos, world, buffer, fluidBlockExtensions.getFluid().getStill().getDefaultState());
		} else {

			BakedModel model = blockRendererDispatcher.getModels().getModel(state.getBlock().getDefaultState());

			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			blockRendererDispatcher.getModelRenderer().render(matrixStack.peek(), immediate.getBuffer(RenderLayers.getEntityBlockLayer(state)), state, model, 1F, 1F, 1F, 1,1);
			matrixStack.pop();
		}


		tessellator.draw();
	}

	@Override
	public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, BlockPos blockPos, Direction direction) {
		if (parent != null) {
			if (blockPos.getX() == parent.getX() && blockPos.getY() == parent.getY() && blockPos.getZ() == parent.getZ()) {
				setMultiblock(null);
			}
		}
		return ActionResult.PASS;
	}

}
