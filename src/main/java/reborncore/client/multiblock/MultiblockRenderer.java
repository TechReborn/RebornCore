/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.client.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.blockentity.MachineBaseBlockEntity;

import java.util.Random;


public class MultiblockRenderer<T extends MachineBaseBlockEntity> extends BlockEntityRenderer<T> {

	private static final BlockPos OUT_OF_WORLD_POS = new BlockPos(0, 260, 0); //such a shitty hack to make local lighting not affect this, but it works for now

	public MultiblockRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	@Override
	public void render(T blockEntity, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
		if (blockEntity.renderMultiblock != null) {
			for (MultiblockComponent comp : blockEntity.renderMultiblock.getComponents()) {
				renderModel(blockEntity, blockEntity.getWorld(), comp.getRelativePosition(), comp.state, matrixStack, vertexConsumerProvider);
			}
		}
	}

	private void renderModel(T blockEntity, World world, BlockPos relativePos, BlockState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
		final BlockPos blockEntityPos = blockEntity.getPos();
		final BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
		matrixStack.push();
		matrixStack.translate(relativePos.getX(), relativePos.getY(), relativePos.getZ());
		matrixStack.translate(0.5, 0.5, 0.5);
		float scale = 0.4F;
		matrixStack.scale(scale, scale, scale);
		matrixStack.translate(-0.5, -0.5, -0.5);

		if (state.getBlock() instanceof FluidBlock) {
			//TODO nope (edit: nope again on 4-Jan-20)
			//FluidState fluidState = ((FluidBlock) state.getBlock()).getFluidState(state);
			//blockRenderManager.renderFluid(new BlockPos(0, 260, 0), world, vertexConsumerProvider.getBuffer(RenderLayers.getFluidLayer(fluidState)), fluidState);
		} else {
			VertexConsumer consumer = vertexConsumerProvider.getBuffer(RenderLayer.getSolid());
			blockRenderManager.renderBlock(state, OUT_OF_WORLD_POS, world, matrixStack, consumer, false, new Random());
		}
		matrixStack.pop();
	}

}
