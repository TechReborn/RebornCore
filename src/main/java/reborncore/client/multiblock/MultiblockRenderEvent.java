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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import reborncore.client.multiblock.component.MultiblockComponent;
import reborncore.common.misc.Location;

import java.util.List;

public class MultiblockRenderEvent {

	public static BlockPos anchor;
	private static BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();
	public MultiblockSet currentMultiblock;
	public Location parent;

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
		ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);

		GlStateManager.pushMatrix();
		GlStateManager.translate(pos.getX() - dx, pos.getY() - dy, pos.getZ() - dz);
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
	}

	private void renderModel(World world, BlockPos pos, int alpha, IBlockState state) {
		IBakedModel model = blockRender.getModelForState(state);
		IBlockState extendedState = state.getBlock().getExtendedState(state, world, pos);
		for (final EnumFacing facing : EnumFacing.values()) {
			this.renderQuads(world, state, pos, model.getQuads(extendedState, facing, 0), alpha);
		}

		this.renderQuads(world, state, pos, model.getQuads(extendedState, null, 0), alpha);
	}

	private void renderQuads(final World world, final IBlockState actualState, final BlockPos pos, final List<BakedQuad> quads, final int alpha) {
		final Tessellator tessellator = Tessellator.getInstance();
		final VertexBuffer buffer = tessellator.getBuffer();

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
			if (event.getPos().getX() == parent.x && event.getPos().getY() == parent.y && event.getPos().getZ() == parent.z
				&& Minecraft.getMinecraft().world == parent.world) {
				setMultiblock(null);
			}
		}
	}

	@SubscribeEvent
	public void worldUnloaded(WorldEvent.Unload event) {
		setMultiblock(null);
	}
}
