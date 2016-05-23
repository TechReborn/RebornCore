package reborncore.shields.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by mark on 14/05/16.
 */
public class ShieldModelLoader {

    public static void load() {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Items.SHIELD, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation("shield", "inventory");
            }
        });
    }

}
