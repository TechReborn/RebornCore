package reborncore.shields;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.registry.GameRegistry;
import reborncore.common.util.LogHelper;
import techreborn.Core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornCoreShields {

    public static TestSheild shield;

    public static void preInit() throws NoSuchFieldException, IllegalAccessException {
        Core.logHelper.info("Replacing the vanilla shield!, bad things might happen");
        shield = new TestSheild();
        GameRegistry.registerItem(shield, "sheild");
        shield = new TestSheild();
        //Gets the field form the Items.class
        Field field = Items.class.getDeclaredField("shield");
        field.setAccessible(true);

        //Removes the final modifier
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        //Sets the new shield over the vanilla one
        field.set(null, shield);

        if(Items.shield.getClass().getCanonicalName().equals(TestSheild.class.getCanonicalName())){
            Core.logHelper.info("Shield has been replaced!");
        } else {
            Core.logHelper.info("Shield has NOT been replaced!! bad things are going to happen");
        }
        System.out.println(Items.shield.getClass());
    }


}
