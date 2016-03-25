package reborncore.shields.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import reborncore.shields.CustomShield;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornItemStackRenderer extends TileEntityItemStackRenderer
{

	private TileEntityBanner banner = new TileEntityBanner();
	private ModelShield modelShield = new ModelShield();

	@Override
	public void renderByItem(ItemStack itemStackIn)
	{
		if (itemStackIn.getItem() == Items.shield)
		{
			if (Items.shield instanceof CustomShield)
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
					GlStateManager.bindTexture(new FileSystemTexture(ShieldTextureLoader.instance.validFiles
							.get(location.getResourcePath().replace("LOOKUP", "") + ".png")).getGlTextureId());
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
				super.renderByItem(itemStackIn);
			}
		} else
		{
			super.renderByItem(itemStackIn);
		}
	}
}
