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
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.util.ItemNBTHelper;
import reborncore.shields.api.Shield;
import reborncore.shields.api.ShieldRegistry;
import reborncore.shields.client.ShieldTextureLoader;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 21/03/2016.
 */
public class CustomShield extends ItemShield implements IEnergyItemInfo
{

	public CustomShield()
	{
		super();
		setHasSubtypes(true);
		setUnlocalizedName("shield");
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getShieldTexture(ItemStack stack)
	{
		if (ItemNBTHelper.getBoolean(stack, "vanilla", true))
		{
			return BannerTextures.SHIELD_BASE_TEXTURE;
		} else
		{
			String str = ItemNBTHelper.getString(stack, "type", "vanilla");
			for(File file : ShieldTextureLoader.instance.validFiles){
				if(file.getName().replace(".png", "").equalsIgnoreCase(str)){
					return new ResourceLocation("LOOKUP:" + str);
				}
			}
			if (ShieldRegistry.shieldHashMap.containsKey(str))
			{
				return ShieldRegistry.shieldTextureHashMap.get(ShieldRegistry.shieldHashMap.get(str));
			}
			return new ResourceLocation("null");
		}
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		ItemStack newStack = new ItemStack(this);
		ItemNBTHelper.setBoolean(newStack, "vanilla", true);
		subItems.add(newStack); // adds vanilla
		for (Shield shield : ShieldRegistry.shieldList)
		{
			if (shield.showInItemLists())
			{
				List<ItemStack> list  = new ArrayList<>();
				shield.getSubTypes(shield, tab, list);
				subItems.addAll(list);
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		super.addInformation(stack, playerIn, tooltip, advanced);
		if (!ItemNBTHelper.getBoolean(stack, "vanilla", true))
		{
			tooltip.add(TextFormatting.BLUE + "Type: " + TextFormatting.GREEN
					+ ItemNBTHelper.getString(stack, "type", "Vanilla"));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if (ItemNBTHelper.getBoolean(stack, "vanilla", true))
		{
			return super.getUnlocalizedName(stack);
		} else
		{
			String str = ItemNBTHelper.getString(stack, "type", "vanilla");
			if (ShieldRegistry.shieldHashMap.containsKey(str))
			{
				return "shield." + ShieldRegistry.shieldHashMap.get(str).name + ".name";
			}
		}
		return super.getUnlocalizedName(stack);
	}

	public @Nullable
	Shield getShieldFromStack(ItemStack stack){
		if(ItemNBTHelper.getBoolean(stack, "vanilla", true)){
			return null;
		} else {
			String str = ItemNBTHelper.getString(stack, "type", "vanilla");
			if (ShieldRegistry.shieldHashMap.containsKey(str)) {
				return ShieldRegistry.shieldHashMap.get(str);
			}
		}
		return null;
	}

	public @Nullable IEnergyItemInfo getEnergyInfoFromShield(ItemStack stack){
		Shield shield = getShieldFromStack(stack);
		if(shield != null){
			if(shield instanceof IEnergyItemInfo){
				return (IEnergyItemInfo) shield;
			}
		}
		return null;
	}

	public IEnergyItemInfo getSafeEnergyFromShield(ItemStack stack){
		IEnergyItemInfo energyItemInfo = getEnergyInfoFromShield(stack);
		if(energyItemInfo == null){
			return new IEnergyItemInfo() {
				@Override
				public double getMaxPower(ItemStack stack) {
					return 0;
				}

				@Override
				public boolean canAcceptEnergy(ItemStack stack) {
					return false;
				}

				@Override
				public boolean canProvideEnergy(ItemStack stack) {
					return false;
				}

				@Override
				public double getMaxTransfer(ItemStack stack) {
					return 0;
				}

				@Override
				public int getStackTier(ItemStack stack) {
					return 0;
				}
			};
		}
		return energyItemInfo;
	}

	@Override
	public double getMaxPower(ItemStack stack) {
		return getSafeEnergyFromShield(stack).getMaxPower(stack);
	}

	@Override
	public boolean canAcceptEnergy(ItemStack stack) {
		return getSafeEnergyFromShield(stack).canAcceptEnergy(stack);
	}

	@Override
	public boolean canProvideEnergy(ItemStack stack) {
		return getSafeEnergyFromShield(stack).canProvideEnergy(stack);
	}

	@Override
	public double getMaxTransfer(ItemStack stack) {
		return getSafeEnergyFromShield(stack).getMaxTransfer(stack);
	}

	@Override
	public int getStackTier(ItemStack stack) {
		return getSafeEnergyFromShield(stack).getStackTier(stack);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		Shield shield = getShieldFromStack(stack);
		if(shield != null){
			return shield.showDurabilityBar(stack);
		}
		return super.showDurabilityBar(stack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		Shield shield = getShieldFromStack(stack);
		if(shield != null){
			return shield.getDurabilityForDisplay(stack);
		}
		return super.getDurabilityForDisplay(stack);
	}
}
