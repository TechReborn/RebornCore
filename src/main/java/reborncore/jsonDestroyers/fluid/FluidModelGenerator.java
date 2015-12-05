package reborncore.jsonDestroyers.fluid;

import com.google.common.base.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ModelFluid;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.api.IFluidTextureProvider;
import reborncore.api.TextureRegistry;
import reborncore.jsonDestroyers.block.CustomTexture;

import java.util.HashMap;

public class FluidModelGenerator {

    public static FluidModelGenerator INSTANCE = new FluidModelGenerator();

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public static HashMap<BlockFluidClassic, TextureAtlasSprite> icons = new HashMap<BlockFluidClassic, TextureAtlasSprite>();

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event) {
        TextureMap textureMap = event.map;
        for (BlockFluidClassic fluid : TextureRegistry.fluids) {
            if (fluid instanceof IFluidTextureProvider) {
                IFluidTextureProvider fluidTextureProvider = (IFluidTextureProvider) fluid;
                String name = fluidTextureProvider.getTextureName();
                TextureAtlasSprite texture = textureMap.getTextureExtry(name);
                if (texture == null) {
                    texture = new CustomTexture(name);
                    textureMap.setTextureEntry(name, texture);
                }
                icons.put(fluid, texture);
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void bakeModels(ModelBakeEvent event) {
        for (final BlockFluidClassic fluid : TextureRegistry.fluids) {
            final ModelResourceLocation fluidLocation = new ModelResourceLocation(fluid.getFluid().getFlowing(), "fluid");
            Item fluidItem = Item.getItemFromBlock(fluid);
            ModelBakery.addVariantName(fluidItem);
            ModelLoader.setCustomMeshDefinition(fluidItem, new ItemMeshDefinition() {
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                    return fluidLocation;
                }
            });
            ModelLoader.setCustomStateMapper(fluid, new StateMapperBase() {
                protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                    return fluidLocation;
                }
            });

            for (int i = 0; i < 16; i++) {
                ModelResourceLocation location = new ModelResourceLocation(TextureRegistry.getBlockResourceLocation(fluid), "level=" + i);
                ModelFluid modelFluid = new ModelFluid(fluid.getFluid());
                Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                    public TextureAtlasSprite apply(ResourceLocation location) {
                        return icons.get(fluid);
                    }
                };
                IFlexibleBakedModel bakedModel = modelFluid.bake(modelFluid.getDefaultState(), DefaultVertexFormats.BLOCK, textureGetter);

                event.modelRegistry.putObject(location, bakedModel);
            }
            ModelResourceLocation inventoryLocation = new ModelResourceLocation(TextureRegistry.getBlockResourceLocation(fluid), "inventory");
            ModelFluid modelFluid = new ModelFluid(fluid.getFluid());
            Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                public TextureAtlasSprite apply(ResourceLocation location) {
                    return icons.get(fluid);
                }
            };
            IFlexibleBakedModel bakedModel = modelFluid.bake(modelFluid.getDefaultState(), DefaultVertexFormats.ITEM, textureGetter);

            event.modelRegistry.putObject(inventoryLocation, bakedModel);
        }
    }


}
