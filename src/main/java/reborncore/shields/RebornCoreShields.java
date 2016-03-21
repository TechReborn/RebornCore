package reborncore.shields;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import reborncore.RebornCore;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornCoreShields {

    public static CustomShield shield;

    public static void preInit() throws NoSuchFieldException, IllegalAccessException {
        RebornCore.logHelper.info("Replacing the vanilla shield!, bad things might happen");
        shield = new CustomShield();
    //    replaceItem(Item.getIdFromItem(Items.shield), shield);
        //Gets the field form the Items.class
        Field field = Items.class.getDeclaredField("shield");
        field.setAccessible(true);

        //Removes the final modifier
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        //Sets the new shield over the vanilla one
        field.set(null, shield);

        if(Items.shield.getClass().getCanonicalName().equals(CustomShield.class.getCanonicalName())){
            RebornCore.logHelper.info("Shield has been replaced!");
        } else {
            RebornCore.logHelper.info("Shield has NOT been replaced!! bad things are going to happen");
        }
        System.out.println(Items.shield.getClass());
    }

    //Thanks covers
    public static void replaceItem(int id, Item item) {
        try {
            ResourceLocation name = Item.itemRegistry.getNameForObject(Item.getItemById(id));
            Item.itemRegistry.registryObjects.put(name, item);
            Item.itemRegistry.underlyingIntegerMap.put(item, id);
            Block block = Block.getBlockById(id);
            if (block != Blocks.air) {
                GameData.getBlockItemMap().put(block, item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
