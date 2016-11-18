package reborncore.shields.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import reborncore.common.util.ItemNBTHelper;
import reborncore.shields.json.ShieldJsonLoader;
import reborncore.shields.json.ShieldUser;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornItemStackRenderer extends TileEntityItemStackRenderer {

    private TileEntityBanner banner = new TileEntityBanner();
    private ModelShield modelShield = new ModelShield();

    private HashMap<String, AbstractTexture> customTextureMap = new HashMap<>();
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());

    TileEntityItemStackRenderer renderer;

    public RebornItemStackRenderer(TileEntityItemStackRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        if (itemStackIn.getItem() == Items.SHIELD) {
            boolean isCustom = !ItemNBTHelper.getBoolean(itemStackIn, "vanilla", false);
            if (isCustom) {
                ResourceLocation location = null;
                String str = ItemNBTHelper.getString(itemStackIn, "type", "vanilla");
                 if(ShieldJsonLoader.shieldJsonFile == null || ShieldJsonLoader.shieldJsonFile.userList == null){
                     renderer.renderByItem(itemStackIn);
                     return;
                 }
                for (ShieldUser user : ShieldJsonLoader.shieldJsonFile.userList) {
                    if (user.username.equalsIgnoreCase(str)) {
                        location = new ResourceLocation("LOOKUP:" + str);
                    }
                }
                if(location == null){
                    renderer.renderByItem(itemStackIn);
                    return;
                }
                ShieldTexture shieldTexture = ShieldTextureStore.getTexture(location);
                if (shieldTexture != null) {
                    if (shieldTexture.getState() == DownloadState.DOWNLOADED) {
                        if (customTextureMap.containsKey(location.getResourcePath())) {
                            Minecraft.getMinecraft().getTextureManager().bindTexture(location);
                        } else {
                            AbstractTexture texture = shieldTexture.getTexture();
                            customTextureMap.put(location.getResourcePath(), texture);
                            final ResourceLocation resourceLocation = location;
                            THREAD_POOL.submit((Runnable) () -> Minecraft.getMinecraft().addScheduledTask((Runnable) () -> Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, texture)));
                            Minecraft.getMinecraft().getTextureManager().bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);
                        }
                    } else {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);
                    }
                } else {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);
                }
            } else {
                renderer.renderByItem(itemStackIn);
                return;
            }
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            modelShield.render();
            GlStateManager.popMatrix();
            return;
        }
        renderer.renderByItem(itemStackIn);

    }
}
