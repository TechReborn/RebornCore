package reborncore.shields;

import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.util.ItemNBTHelper;
import reborncore.shields.api.Shield;
import reborncore.shields.api.ShieldRegistry;

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
        if(ItemNBTHelper.getBoolean(stack, "vanilla", true)){
            return BannerTextures.SHIELD_BASE_TEXTURE;
        } else {
            String str = ItemNBTHelper.getString(stack, "type", "vanilla");
            if(ShieldRegistry.shieldHashMap.containsKey(str)){
                return ShieldRegistry.shieldTextureHashMap.get(ShieldRegistry.shieldHashMap.get(str));
            }
            return new ResourceLocation("null");
        }
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack newStack = new ItemStack(this);
        ItemNBTHelper.setBoolean(newStack, "vanilla", true);
        subItems.add(newStack); //adds vanilla
        for(Shield shield : ShieldRegistry.shieldList){
            if(shield.showInItemLists()){
                newStack = new ItemStack(this);
                ItemNBTHelper.setString(newStack, "type", shield.name);
                ItemNBTHelper.setBoolean(newStack, "vanilla", false);
                subItems.add(newStack);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        if(!ItemNBTHelper.getBoolean(stack, "vanilla", true)){
            tooltip.add(TextFormatting.BLUE + "Type: " + TextFormatting.GREEN + ItemNBTHelper.getString(stack, "type", "Vanilla"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if(ItemNBTHelper.getBoolean(stack, "vanilla", true)){
            return super.getUnlocalizedName(stack);
        } else {
            String str = ItemNBTHelper.getString(stack, "type", "vanilla");
            if(ShieldRegistry.shieldHashMap.containsKey(str)){
                return "shield." + ShieldRegistry.shieldHashMap.get(str).name + ".name";
            }
        }
        return super.getUnlocalizedName(stack);
    }

}
