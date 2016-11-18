package reborncore.shields.api;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import reborncore.common.util.ItemNBTHelper;

import java.util.List;

/**
 * Created by Mark on 21/03/2016.
 */
public abstract class Shield {

	public String name;

	public Shield(String name) {
		this.name = name;
	}

	public ResourceLocation getShieldTexture() {
		return new ResourceLocation("null");
	}

	public boolean showInItemLists() {
		return true;
	}

	public void getSubTypes(Shield shield, CreativeTabs tab, List<ItemStack> subItems) {
		ItemStack newStack = new ItemStack(Items.SHIELD);
		ItemNBTHelper.setString(newStack, "type", shield.name);
		ItemNBTHelper.setBoolean(newStack, "vanilla", false);
		subItems.add(newStack);
	}

	public double getDurabilityForDisplay(ItemStack stack) {
		return (double) stack.getItemDamage() / (double) stack.getMaxDamage();
	}

	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

}
