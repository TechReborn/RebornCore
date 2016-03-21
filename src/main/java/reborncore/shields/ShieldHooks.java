package reborncore.shields;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by Mark on 21/03/2016.
 */
public class ShieldHooks {

    private static ModelShield modelShield = new ModelShield();

    public static void renderByItem(ItemStack itemStackIn) {
        if(itemStackIn.getItem() == Items.shield){
            System.out.println();
            CustomShield sheild = (CustomShield) itemStackIn.getItem();
            Minecraft.getMinecraft().getTextureManager().bindTexture(sheild.getShieldTexture(itemStackIn));
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            modelShield.render();
            GlStateManager.popMatrix();
            return;
        }

    }
}
