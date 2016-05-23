package reborncore.shields;

import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import reborncore.common.util.ItemNBTHelper;
import reborncore.shields.api.Shield;
import reborncore.shields.api.ShieldRegistry;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornCoreShields {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new RebornCoreShields());
    }

    @SubscribeEvent
    public void craft(PlayerEvent.ItemCraftedEvent event) {
        if (event.crafting.getItem() == Items.SHIELD) {
            for (Shield shield : ShieldRegistry.shieldList) {
                if (shield.name.equalsIgnoreCase(event.player.getName())) {
                    ItemNBTHelper.setString(event.crafting, "type", shield.name);
                    ItemNBTHelper.setBoolean(event.crafting, "vanilla", false);
                }
            }
        }
    }

}
