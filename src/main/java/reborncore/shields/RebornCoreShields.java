package reborncore.shields;


import mezz.jei.JeiHelpers;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
    }

}
