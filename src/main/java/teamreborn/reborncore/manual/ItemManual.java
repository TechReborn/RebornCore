package teamreborn.reborncore.manual;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import teamreborn.reborncore.RCConstants;
import teamreborn.reborncore.RebornCore;
import teamreborn.reborncore.api.registry.RebornRegistry;
import teamreborn.reborncore.api.registry.impl.ItemRegistry;

/**
 * File Created by Prospector.
 */
@RebornRegistry(RCConstants.MOD_ID)
public class ItemManual extends Item {

	@ItemRegistry
	public static ItemManual manual;

	public ItemManual() {
		super();
		setRegistryName(RCConstants.MOD_ID, "team_reborn_manual");
		setUnlocalizedName(RCConstants.MOD_ID + ":team_reborn_manual");
		setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.openGui(RebornCore.instance, 0, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posY);
		return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
}
