package reborncore.jsonDestroyers.block;


import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.RebornCore;
import reborncore.api.IBlockTextureProvider;
import reborncore.api.TextureRegistry;

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
                for (int i = 0; i < blockTextureProvider.amountOfVariants(); i++) {
                    for (EnumFacing side : EnumFacing.values()) {
                        String name = blockTextureProvider.getTextureName(blockTextureProvider.getStateFromMeta(i), side);
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
            if ( block instanceof IBlockTextureProvider) {
                IBlockTextureProvider textureProvdier = (IBlockTextureProvider) block;
                for (int i = 0; i < textureProvdier.amountOfVariants(); i++) {
                    HashMap<EnumFacing, TextureAtlasSprite> textureMap = new HashMap<EnumFacing, TextureAtlasSprite>();
                    for (EnumFacing side : EnumFacing.VALUES) {
                        for (BlockIconInfo iconInfo : blockIconInfoList) {
                            if (iconInfo.getBlock() == block && iconInfo.getMeta() == i && iconInfo.getSide() == side) {
                                textureMap.put(side, icons.get(iconInfo));
                            }
                        }
                    }

                    BlockModel model = new BlockModel(textureMap, block.getStateFromMeta(i));

                    ModelResourceLocation modelResourceLocation = TextureRegistry.getModelResourceLocation(block.getStateFromMeta(i));

                    event.modelRegistry.putObject(modelResourceLocation, model);

                    ModelResourceLocation inventory = TextureRegistry.getBlockinventoryResourceLocation(block);
                    event.modelRegistry.putObject(inventory, model);
                    itemModelMesher.register(Item.getItemFromBlock(block), i, inventory);

                    event.modelRegistry.putObject(modelResourceLocation, model);
                    itemModelMesher.register(Item.getItemFromBlock(block), i, modelResourceLocation);
                }

            } else {
                RebornCore.logHelper.debug("Block does not extend blockBase!");
            }
        }
    }
}
