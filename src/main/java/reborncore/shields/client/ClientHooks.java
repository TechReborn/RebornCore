package reborncore.shields.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import reborncore.shields.CustomShield;

import java.util.UUID;

/**
 * Created by Mark on 21/03/2016.
 */
public class ClientHooks {

    private static TileEntityChest chestBasic = new TileEntityChest(BlockChest.Type.BASIC);
    private static TileEntityChest chestTrap = new TileEntityChest(BlockChest.Type.TRAP);
    private static TileEntityEnderChest enderChest = new TileEntityEnderChest();
    private static TileEntityBanner banner = new TileEntityBanner();
    private static TileEntitySkull skull = new TileEntitySkull();
    private static ModelShield modelShield = new ModelShield();

    public static void renderByItem(ItemStack itemStackIn) {
        if (itemStackIn.getItem() == Items.shield) {
            CustomShield sheild = (CustomShield) itemStackIn.getItem();
            Minecraft.getMinecraft().getTextureManager().bindTexture(sheild.getShieldTexture(itemStackIn));
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            modelShield.render();
            GlStateManager.popMatrix();
            return;
        }
        if (itemStackIn.getItem() == Items.banner) {
            banner.setItemValues(itemStackIn);
            TileEntityRendererDispatcher.instance.renderTileEntityAt(banner, 0.0D, 0.0D, 0.0D, 0.0F);
        } else if (itemStackIn.getItem() == Items.skull) {
            GameProfile gameprofile = null;

            if (itemStackIn.hasTagCompound()) {
                NBTTagCompound nbttagcompound = itemStackIn.getTagCompound();

                if (nbttagcompound.hasKey("SkullOwner", 10)) {
                    gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                } else if (nbttagcompound.hasKey("SkullOwner", 8) && !nbttagcompound.getString("SkullOwner").isEmpty()) {
                    GameProfile lvt_2_2_ = new GameProfile((UUID) null, nbttagcompound.getString("SkullOwner"));
                    gameprofile = TileEntitySkull.updateGameprofile(lvt_2_2_);
                    nbttagcompound.removeTag("SkullOwner");
                    nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                }
            }

            if (TileEntitySkullRenderer.instance != null) {
                GlStateManager.pushMatrix();
                GlStateManager.disableCull();
                TileEntitySkullRenderer.instance.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 0.0F, itemStackIn.getMetadata(), gameprofile, -1, 0.0F);
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        } else {
            Block block = Block.getBlockFromItem(itemStackIn.getItem());

            if (block == Blocks.ender_chest) {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(enderChest, 0.0D, 0.0D, 0.0D, 0.0F);
            } else if (block == Blocks.trapped_chest) {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(chestTrap, 0.0D, 0.0D, 0.0D, 0.0F);
            } else if (block != Blocks.chest)
                net.minecraftforge.client.ForgeHooksClient.renderTileItem(itemStackIn.getItem(), itemStackIn.getMetadata());
            else {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(chestBasic, 0.0D, 0.0D, 0.0D, 0.0F);
            }
        }
    }
}
