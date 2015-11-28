package reborncore.jsonDestroyers.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;
import reborncore.api.TextureRegistry;

public class FluidModelGenerator {


    public static void postInit(){
        for(BlockFluidClassic fluid : TextureRegistry.fluids){
            final ModelResourceLocation fluidLocation = new ModelResourceLocation(fluid.getFluid().getFlowing(), "fluid");
            Item fluidItem = Item.getItemFromBlock(fluid);
            ModelBakery.addVariantName(fluidItem);
            ModelLoader.setCustomMeshDefinition(fluidItem, new ItemMeshDefinition() {
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                    return fluidLocation;
                }
            });

            ModelLoader.setCustomStateMapper(fluid, new StateMapperBase()
            {
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return fluidLocation;
                }
            });
        }
    }


}
