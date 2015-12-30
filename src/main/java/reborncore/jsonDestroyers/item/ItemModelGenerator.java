package reborncore.jsonDestroyers.item;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.api.IItemTexture;
import reborncore.api.TextureRegistry;
import reborncore.jsonDestroyers.block.CustomTexture;

import java.util.ArrayList;
import java.util.List;


public class ItemModelGenerator {
    public static ItemModelGenerator INSTANCE = new ItemModelGenerator();

    public static List<ItemIconInfo> icons = new ArrayList<ItemIconInfo>();

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event) {
        TextureMap textureMap = event.map;

        for (Item item : TextureRegistry.items) {
            if (item instanceof IItemTexture) {
                IItemTexture ItemTexture = (IItemTexture) item;
                for (int i = 0; i < ItemTexture.getMaxMeta(); i++) {
                    String name = ItemTexture.getTextureName(i);
                    TextureAtlasSprite texture = textureMap.getTextureExtry(name);
                    if (texture == null) {
                        texture = new CustomTexture(name);
                        textureMap.setTextureEntry(name, texture);
                    }
                    ItemIconInfo info = new ItemIconInfo(item, i, texture, name);
                    icons.add(info);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void bakeModels(ModelBakeEvent event) {
        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        for (Item item : TextureRegistry.items) {
            if (item instanceof IItemTexture) {
                IItemTexture textureProvdier = (IItemTexture) item;
                for (int i = 0; i < textureProvdier.getMaxMeta(); i++) {
                    TextureAtlasSprite texture = null;
                    ItemIconInfo itemIconInfo = null;
                    for (ItemIconInfo info : icons) {
                        if (info.damage == i && info.getItem() == item) {
                            texture = info.getSprite();
                            itemIconInfo = info;
                            break;
                        }
                    }
                    if (texture == null) {
                        break;
                    }
                    ModelResourceLocation inventory;
                    if(textureProvdier.getMaxMeta() == 1){
                        inventory = TextureRegistry.getItemInventoryResourceLocation(item);
                    } else {
                        inventory = new ModelResourceLocation(textureProvdier.getModID() + ":" + item.getUnlocalizedName(new ItemStack(item, 1, i)).substring(5), "inventory");
                    }

                    final TextureAtlasSprite finalTexture = texture;
                    Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                        public TextureAtlasSprite apply(ResourceLocation location) {
                            return finalTexture;
                        }
                    };
                    ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
                    builder.add(new ResourceLocation(itemIconInfo.textureName));
                    ItemLayerModel itemLayerModel = new ItemLayerModel(builder.build());
                    IBakedModel model = itemLayerModel.bake(ItemLayerModel.instance.getDefaultState(), DefaultVertexFormats.ITEM, textureGetter);
                    event.modelRegistry.putObject(inventory, model);
                    itemModelMesher.register(item, i, inventory);

                }
            }
        }
    }


}
