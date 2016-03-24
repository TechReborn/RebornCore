package reborncore.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class TorchHelper
{
	public static EnumActionResult placeTorch(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumFacing side, float xOffset, float yOffset, float zOffset, EnumHand hand)
	{
		for (int i = 0; i < player.inventory.mainInventory.length; i++)
		{
			ItemStack torchStack = player.inventory.mainInventory[i];
			if (torchStack == null || !torchStack.getUnlocalizedName().toLowerCase().contains("torch"))
				continue;
			Item item = torchStack.getItem();
			if (!(item instanceof ItemBlock))
				continue;
			int oldMeta = torchStack.getItemDamage();
			int oldSize = torchStack.stackSize;
			EnumActionResult result = torchStack.onItemUse(player, world, pos, hand, side, xOffset, yOffset, zOffset);
			if (player.capabilities.isCreativeMode)
			{
				torchStack.setItemDamage(oldMeta);
				torchStack.stackSize = oldSize;
			} else if (torchStack.stackSize <= 0)
			{
				ForgeEventFactory.onPlayerDestroyItem(player, torchStack, hand);
				player.inventory.mainInventory[i] = null;
			}
			if (result == EnumActionResult.SUCCESS)
			{
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}
}
