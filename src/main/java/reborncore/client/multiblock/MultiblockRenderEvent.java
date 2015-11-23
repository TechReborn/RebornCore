/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p/>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package reborncore.client.multiblock;

import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
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
            mc.thePlayer.getCurrentEquippedItem();
            renderPlayerLook(mc.thePlayer, mc.objectMouseOver);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (currentMultiblock != null && anchor == null && event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer == Minecraft.getMinecraft().thePlayer) {
            anchor = new CoordTriplet(event.pos);
            angle = MathHelper.floor_double(event.entityPlayer.rotationYaw * 4.0 / 360.0 + 0.5) & 3;
            event.setCanceled(true);
        }
    }

    private void renderPlayerLook(EntityPlayer player, MovingObjectPosition src) {
        if (currentMultiblock != null) {
            int anchorX = anchor != null ? anchor.x : src.getBlockPos().getX();
            int anchorY = anchor != null ? anchor.y + 1 : src.getBlockPos().getY() + 1;
            int anchorZ = anchor != null ? anchor.z : src.getBlockPos().getZ();

            Multiblock mb = currentMultiblock.getForEntity(player);
            for (MultiblockComponent comp : mb.getComponents())
                renderComponent(player.worldObj, mb, comp, anchorX, anchorY, anchorZ);
        }
    }

    private boolean renderComponent(World world, Multiblock mb, MultiblockComponent comp, int anchorX, int anchorY, int anchorZ) {
        CoordTriplet pos = comp.getRelativePosition();
        int x = pos.x + anchorX;
        int y = pos.y + anchorY;
        int z = pos.z + anchorZ;

        if (world.getBlockState(new BlockPos(x, y, z)) == comp.getBlock())
            return false;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1F, 1F, 1F, 0.4F);
        //GL11.glTranslated(x + 0.5 - RenderManager.renderPosX, y + 0.5 - RenderManager.renderPosY, z + 0.5 - RenderManager.renderPosZ);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        Block block = comp.getBlock();
        if (IMultiblockRenderHook.renderHooks.containsKey(block))
            IMultiblockRenderHook.renderHooks.get(block).renderBlockForMultiblock(world, mb, block, comp.getMeta(), blockRender);
        else blockRender.renderBlock(block.getDefaultState(), new BlockPos(x, y, z), world, Tessellator.getInstance().getWorldRenderer());
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
        return true;
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        if (partent != null) {
            if (event.pos.getX() == partent.x && event.pos.getY() == partent.y && event.pos.getZ() == partent.z && Minecraft.getMinecraft().theWorld == partent.world) {
                setMultiblock(null);
            }
        }
    }

    @SubscribeEvent
    public void worldUnloaded(WorldEvent.Unload event) {
        setMultiblock(null);
    }
}