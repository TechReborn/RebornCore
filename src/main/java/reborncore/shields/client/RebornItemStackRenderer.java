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
import reborncore.client.texture.FileSystemTexture;
import reborncore.client.texture.InputStreamTexture;
import reborncore.shields.CustomShield;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornItemStackRenderer extends TileEntityItemStackRenderer
{

	private TileEntityBanner banner = new TileEntityBanner();
	private ModelShield modelShield = new ModelShield();

	private HashMap<String, AbstractTexture> customTextureMap = new HashMap<>();

	TileEntityItemStackRenderer renderer;

	public RebornItemStackRenderer(TileEntityItemStackRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void renderByItem(ItemStack itemStackIn)
	{
		if (itemStackIn.getItem() == Items.SHIELD)
		{
			if (Items.SHIELD instanceof CustomShield)
			{
				CustomShield sheild = (CustomShield) itemStackIn.getItem();
				ResourceLocation location = sheild.getShieldTexture(itemStackIn);
				if (itemStackIn.getSubCompound("BlockEntityTag", false) != null)
				{
					banner.setItemValues(itemStackIn);
					Minecraft.getMinecraft().getTextureManager()
							.bindTexture(BannerTextures.SHIELD_DESIGNS.getResourceLocation(
									banner.getPatternResourceLocation(), banner.getPatternList(),
									banner.getColorList()));
				} else if (location.getResourceDomain().equals("lookup"))
				{
					ShieldTexture shieldTexture = ShieldTextureStore.getTexture(location);
					if(shieldTexture.getState() == DownloadState.DOWNLOADED){
						AbstractTexture texture = null;
						if(customTextureMap.containsKey(location.getResourcePath())){
							texture = customTextureMap.get(location.getResourcePath());
						} else {
							texture = shieldTexture.getTexture();
							customTextureMap.put(location.getResourcePath(), texture);
							Minecraft.getMinecraft().getTextureManager().loadTexture(location, texture);
						}
						Minecraft.getMinecraft().getTextureManager().bindTexture(location);
					} else {
						Minecraft.getMinecraft().getTextureManager().bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);
					}
				} else
				{
					Minecraft.getMinecraft().getTextureManager().bindTexture(location);
				}
				GlStateManager.pushMatrix();
				GlStateManager.scale(1.0F, -1.0F, -1.0F);
				modelShield.render();
				GlStateManager.popMatrix();
				return;
			} else
			{
				renderer.renderByItem(itemStackIn);
			}
		} else
		{
			renderer.renderByItem(itemStackIn);
		}
	}
}
