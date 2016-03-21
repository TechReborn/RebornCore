package reborncore.shields;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.util.ItemNBTHelper;

import java.util.List;

/**
 * Created by Mark on 21/03/2016.
 */
public class CustomShield extends ItemShield {

    public CustomShield() {
        super();
        setHasSubtypes(true);
        setUnlocalizedName("shield");
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getShieldTexture(ItemStack stack){
        if(ItemNBTHelper.verifyExistance(stack, "vanilla")){
            return SheildTextures.vanillaShield;
        } else {
            return SheildTextures.testShield;
        }
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        super.getSubItems(itemIn, tab, subItems);
        ItemStack newStack = new ItemStack(this);
        ItemNBTHelper.setBoolean(newStack, "vanilla", true);
        subItems.add(newStack);
    }
}
