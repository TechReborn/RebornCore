package reborncore.jsonDestroyers.block;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.RebornCore;
import reborncore.api.IBlockTextureProvider;
import reborncore.api.TextureRegistry;
import reborncore.common.BaseBlock;

import java.util.ArrayList;
import java.util.HashMap;

public class ModelGenertator {

    public static ModelGenertator INSTANCE = new ModelGenertator();
    public static HashMap<BlockIconInfo, TextureAtlasSprite> icons = new HashMap<BlockIconInfo, TextureAtlasSprite>();
    public static ArrayList<BlockIconInfo> blockIconInfoList = new ArrayList<BlockIconInfo>();

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event) {
        TextureMap textureMap = event.map;
        for (Block block : TextureRegistry.blocks) {
            if (block instanceof IBlockTextureProvider) {
                IBlockTextureProvider blockTextureProvider = (IBlockTextureProvider) block;
                for (int i = 0; i < blockTextureProvider.amoutOfVariants(); i++) {
                    for (EnumFacing side : EnumFacing.values()) {
                        String name = CustomTexture.getDerivedName(blockTextureProvider.getTextureName(blockTextureProvider.getStateFromMeta(i), side));
                        TextureAtlasSprite texture = textureMap.getTextureExtry(name);
                        if (texture == null) {
                            texture = new CustomTexture(name);
                            textureMap.setTextureEntry(name, texture);
                        }
                        BlockIconInfo iconInfo = new BlockIconInfo(block, i, side);
                        icons.put(iconInfo, texture);
                        blockIconInfoList.add(iconInfo);
                    }
                }
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void bakeModels(ModelBakeEvent event) {
        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        for (Block block : TextureRegistry.blocks) {
            if (block instanceof BaseBlock && block instanceof IBlockTextureProvider) {
                BaseBlock baseBlock = (BaseBlock) block;
                IBlockTextureProvider textureProvdier = (IBlockTextureProvider) block;
                baseBlock.models = new IBakedModel[textureProvdier.amoutOfVariants()];
                baseBlock.invmodels = new IBakedModel[textureProvdier.amoutOfVariants()];
                for (int i = 0; i < textureProvdier.amoutOfVariants(); i++) {
                    HashMap<EnumFacing, TextureAtlasSprite> textureMap = new HashMap<EnumFacing, TextureAtlasSprite>();
                    for (EnumFacing side : EnumFacing.VALUES) {
                        for (BlockIconInfo iconInfo : blockIconInfoList) {
                            if (iconInfo.getBlock() == block && iconInfo.getMeta() == i && iconInfo.getSide() == side) {
                                textureMap.put(side, icons.get(iconInfo));
                            }
                        }
                    }


                    /// * Block model *
                    // get the model registries entry for the current  block state
                    ModelResourceLocation modelResourceLocation = ModelBuilder.getModelResourceLocation(block.getStateFromMeta(i));



                    event.modelRegistry.putObject(modelResourceLocation, new BlockModel(textureMap, block.getStateFromMeta(i)));

                    System.out.println(modelResourceLocation);




                    baseBlock.invmodels[i] = new BlockModel(textureMap, block.getStateFromMeta(i + 1));

                    ModelResourceLocation inventory = new ModelResourceLocation(modelResourceLocation, "inventory");

                    event.modelRegistry.putObject(inventory, baseBlock.invmodels[i]);

                    itemModelMesher.register(Item.getItemFromBlock(block), i, inventory);
                }

            } else {
                RebornCore.logHelper.debug("Block does not extend blockBase!");
            }
        }
    }
}
