package reborncore.shields;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import reborncore.common.util.ItemNBTHelper;
import reborncore.shields.api.ShieldRegistry;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornCoreShields {

    public static CustomShield shield = new CustomShield();

    public static void init(){
        ShieldRegistry.registerShield(new FaceShield("modmuss50"));
        ShieldRegistry.registerShield(new FaceShield("gigabit101"));
        ShieldRegistry.registerShield(new FaceShield("AKTheKnight"));
        ShieldRegistry.registerShield(new FaceShield("ProfProspector"));
        ShieldRegistry.registerShield(new FaceShield("nexans"));

        MinecraftForge.EVENT_BUS.register(new RebornCoreShields());
    }

    @SubscribeEvent
    public void craft(PlayerEvent.ItemCraftedEvent event){
        if(ShieldRegistry.shieldHashMap.containsKey(event.player.getName())){
            ItemNBTHelper.setString(event.crafting, "type", event.player.getName());
            ItemNBTHelper.setBoolean(event.crafting, "vanilla", false);
        }
    }

}
