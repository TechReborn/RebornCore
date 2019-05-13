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

//TODO a lot of 1.14 work needed here
public class MultiblockRenderEvent {

//	public static BlockPos anchor;
//	//private static BlockRendererDispatcher blockRender = Minecraft.getInstance().getBlockRendererDispatcher();
	public MultiblockSet currentMultiblock;
//	//public Location parent;
//	public BlockPos parent;
//	RebornFluidRenderer fluidRenderer;
//	private VisibleRegion camera;
//
//	public MultiblockRenderEvent() {
//		//this.fluidRenderer = new RebornFluidRenderer();
//	}
//
//	public void setMultiblock(MultiblockSet set) {
//		currentMultiblock = set;
//		anchor = null;
//		parent = null;
//	}
//
//	@SubscribeEvent
//	public void onWorldRenderLast(RenderWorldLastEvent event) {
//		MinecraftClient mc = MinecraftClient.getInstance();
//		if (mc.player != null && mc.hitResult != null && !mc.player.isSneaking()) {
//			if (currentMultiblock != null) {
//				BlockPos anchorPos = anchor != null ? anchor : mc.hitResult.getBlockPos();
//
//				Multiblock mb = currentMultiblock.getForIndex(0);
//
//				//TODO 1.13 fluid rendering
//				//Render the liquids first, it looks better.
//				for (MultiblockComponent comp : mb.getComponents()) {
//					//	if (comp.state.getRenderType() == EnumBlockRenderType.LIQUID) {
//					renderComponent(comp, anchorPos.up(), event.getPartialTicks(), mc.player);
//					//	}
//				}
//				//				for (MultiblockComponent comp : mb.getComponents()) {
//				//					if (comp.state.getRenderType() != EnumBlockRenderType.LIQUID) {
//				//						renderComponent(comp, anchorPos.up(), event.getPartialTicks(), mc.player);
//				//					}
//				//				}
//
//			}
//		}
//	}
//
//	private void renderComponent(MultiblockComponent comp, BlockPos anchor, float partialTicks, ClientPlayerEntity player) {
//		double dx = player.prevRenderX + (player.x - player.prevRenderX) * partialTicks;
//		double dy = player.prevRenderY + (player.y - player.prevRenderY) * partialTicks;
//		double dz = player.prevRenderZ + (player.z - player.prevRenderZ) * partialTicks;
//		if (camera == null) {
//			camera = new FrustumWithOrigin();
//		}
//		camera.setOrigin(dx, dy, dz);
//		BlockPos pos = anchor.add(comp.getRelativePosition());
//		if (!camera.intersects(new BoundingBox(pos))) {
//			return;
//		}
//		MinecraftClient minecraft = MinecraftClient.getInstance();
//		World world = player.world;
//
//		minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
//		BlockRenderLayer originalLayer = MinecraftForgeClient.getRenderLayer();
//		ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);
//
//		GlStateManager.pushMatrix();
//		GlStateManager.translated(-dx, -dy, -dz);
//		GlStateManager.translated(pos.getX(), pos.getY(), pos.getZ());
//		GlStateManager.scaled(0.8, 0.8, 0.8);
//		GlStateManager.translated(0.2, 0.2, 0.2);
//
//		GuiLighting.disable();
//		GlStateManager.enableBlend();
//
//		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.CONSTANT_ALPHA);
//		GL14.glBlendColor(1F, 1F, 1F, 0.35F);
//
//		this.renderModel(world, pos, comp.state);
//		GlStateManager.disableBlend();
//		GlStateManager.popMatrix();
//		ForgeHooksClient.setRenderLayer(originalLayer);
//	}
//
//	private void renderModel(World world, BlockPos pos, BlockState state) {
//		final BlockRenderManager blockRendererDispatcher = MinecraftClient.getInstance().getBlockRenderManager();
//		final Tessellator tessellator = Tessellator.getInstance();
//		final BufferBuilder buffer = tessellator.getBufferBuilder();
//		GlStateManager.translated(-pos.getX(), -pos.getY(), -pos.getZ());
//		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_UV_LMAP);
//		//TODO 1.13 fluids
//		//		if (state.getRenderType() == EnumBlockRenderType.LIQUID) {
//		//			fluidRenderer.renderFluid(world, state, pos, buffer);
//		//		} else {
//		blockRendererDispatcher.renderBlock(state, pos, world, buffer, new Random());
//		//	}
//		tessellator.draw();
//	}
//
//	@SubscribeEvent
//	public void breakBlock(BlockEvent.BreakEvent event) {
//		if (parent != null) {
//			if (event.getPos().getX() == parent.getX() && event.getPos().getY() == parent.getY() && event.getPos().getZ() == parent.getZ()) {
//				setMultiblock(null);
//			}
//		}
//	}
//
//	@SubscribeEvent
//	public void worldUnloaded(WorldEvent.Unload event) {
//		setMultiblock(null);
//	}
}
