package reborncore.modcl.manual;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import reborncore.RebornCore;

/**
 * Created by Prospector
 */
public class ItemTeamRebornManual extends Item {
	public ItemTeamRebornManual() {
		setUnlocalizedName("reborncore:manual");
		setRegistryName(RebornCore.MOD_ID, "manual");
		setCreativeTab(CreativeTabs.TOOLS);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.openGui(RebornCore.INSTANCE, 0, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posY);
		return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
}
