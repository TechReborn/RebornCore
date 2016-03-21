package reborncore.shields;

import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 * Created by Mark on 21/03/2016.
 */
public class ShieldHooks {

    public static void registerItem(int id, ResourceLocation textualID, Item itemIn) {
        if(itemIn.getClass().getCanonicalName().equals(ItemShield.class.getCanonicalName())){
            FMLLog.log("RebornCore", Level.INFO, String.valueOf("Replacing the vanilla shield!"));
            itemIn = new CustomShield();
        }
        Item.itemRegistry.register(id, textualID, itemIn);
    }
}
