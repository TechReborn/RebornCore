/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */

package reborncore.client.multiblock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import reborncore.client.multiblock.component.MultiblockComponent;
import reborncore.common.misc.Location;
import reborncore.common.multiblock.CoordTriplet;

public class MultiblockRenderEvent {

	private static BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();
	public MultiblockSet currentMultiblock;
	public static CoordTriplet anchor;
	public Location partent;
	public static int angle;

	public void setMultiblock(MultiblockSet set) {
		currentMultiblock = set;
		anchor = null;
		angle = 0;
		partent = null;
	}

	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer != null && mc.objectMouseOver != null && !mc.thePlayer.isSneaking()) {
			renderPlayerLook(mc.thePlayer, mc.objectMouseOver);
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		//TODO test code, needs work
		if(currentMultiblock == null){
			Multiblock multiblock = new Multiblock();
			for (int i = 0; i < 5; i++) {
				multiblock.addComponent(new BlockPos(0, i, 0), Blocks.DIAMOND_BLOCK.getDefaultState());
			}
			currentMultiblock = new MultiblockSet(multiblock);
		}

		if (currentMultiblock != null && anchor == null && event.getHand() == EnumHand.MAIN_HAND
			&& event.getEntityPlayer() == Minecraft.getMinecraft().thePlayer) {
			anchor = new CoordTriplet(event.getPos());
			angle = MathHelper.floor_double(event.getEntityPlayer().rotationYaw * 4.0 / 360.0 + 0.5) & 3;
			event.setCanceled(true);
		}
	}

	private void renderPlayerLook(EntityPlayer player, RayTraceResult src) {
		if (currentMultiblock != null) {
			int anchorX = anchor != null ? anchor.x : src.getBlockPos().getX();
			int anchorY = anchor != null ? anchor.y + 1 : src.getBlockPos().getY() + 1;
			int anchorZ = anchor != null ? anchor.z : src.getBlockPos().getZ();
			Multiblock mb = currentMultiblock.getForEntity(player);
			for (MultiblockComponent comp : mb.getComponents()){
				renderComponent(player.worldObj, mb, comp, anchorX, anchorY, anchorZ);
			}
		}
	}

	private boolean renderComponent(World world, Multiblock mb, MultiblockComponent comp, int anchorX, int anchorY,
	                                int anchorZ) {
		//TODO RENDER THINGS
		return true;
	}

	@SubscribeEvent
	public void breakBlock(BlockEvent.BreakEvent event) {
		if (partent != null) {
			if (event.getPos().getX() == partent.x && event.getPos().getY() == partent.y && event.getPos().getZ() == partent.z
				&& Minecraft.getMinecraft().theWorld == partent.world) {
				setMultiblock(null);
			}
		}
	}

	@SubscribeEvent
	public void worldUnloaded(WorldEvent.Unload event) {
		setMultiblock(null);
	}
}