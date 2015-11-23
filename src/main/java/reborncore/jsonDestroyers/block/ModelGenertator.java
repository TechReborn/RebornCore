package reborncore.jsonDestroyers.block;


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
import reborncore.RebornCore;

public class ModelGenertator {

    public static ModelGenertator INSTANCE = new ModelGenertator();
    public static TextureAtlasSprite icon;


    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event) {
        TextureMap textureMap = event.map;
        String name = "reborncore:blocks/test";
        TextureAtlasSprite texture = textureMap.getTextureExtry(name);
        if (texture == null) {
            texture = new CustomTexture(name);
            textureMap.setTextureEntry(name, texture);
        }
        icon = textureMap.getTextureExtry(name);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void bakeModels(ModelBakeEvent event) {
        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        ModelResourceLocation modelResourceLocation = ModelBuilder.getModelResourceLocation(RebornCore.test.getDefaultState());
        IBakedModel baseModel = event.modelManager.getBlockModelShapes().getModelForState(RebornCore.test.getDefaultState());
        RebornCore.test.model = ModelBuilder.changeIcon(baseModel, icon);
        event.modelRegistry.putObject(modelResourceLocation, RebornCore.test.model);
        IBakedModel itemModel = itemModelMesher.getItemModel(new ItemStack(RebornCore.test));
        RebornCore.test.invmodel = ModelBuilder.changeIcon(itemModel, icon);
        ModelResourceLocation inventory = new ModelResourceLocation(modelResourceLocation, "inventory");
        event.modelRegistry.putObject(inventory, RebornCore.test.invmodel);
        itemModelMesher.register(Item.getItemFromBlock(RebornCore.test), 0, inventory);

    }
}
