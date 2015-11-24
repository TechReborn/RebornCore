package reborncore.jsonDestroyers.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
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
                    ItemIconInfo info = new ItemIconInfo(item, i, texture);
                    icons.add(info);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void bakeModels(ModelBakeEvent event) {
        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        for (Item item : TextureRegistry.items) {
            if (item instanceof IItemTexture && item instanceof BaseItem) {
                IItemTexture textureProvdier = (IItemTexture) item;
                BaseItem baseItem = (BaseItem) item;
                baseItem.invmodels = new IBakedModel[textureProvdier.getMaxMeta()];
                for (int i = 0; i < textureProvdier.getMaxMeta(); i++) {
                    TextureAtlasSprite texture = null;
                    for (ItemIconInfo info : icons) {
                        if (info.damage == i && info.getItem() == item) {
                            texture = info.getSprite();
                            break;
                        }
                    }
                    if (texture == null) {
                        break;
                    }
                    ModelResourceLocation inventory = new ModelResourceLocation(textureProvdier.getModID() + ":" + item.getUnlocalizedName(new ItemStack(item, 1, i)).substring(5), "inventory");
                    event.modelRegistry.putObject(inventory, new ItemModel(texture));
                    itemModelMesher.register(baseItem, i, inventory);
                }

            }
        }
    }


}
