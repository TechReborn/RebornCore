package reborncore.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class TorchHelper 
{
    public static boolean placeTorch(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float xOffset, float yOffset, float zOffset) 
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
            boolean result = torchStack.onItemUse(player, world, pos, side, xOffset, yOffset, zOffset);
            if (player.capabilities.isCreativeMode) 
            {
                torchStack.setItemDamage(oldMeta);
                torchStack.stackSize = oldSize;
            } 
            else if (torchStack.stackSize <= 0) 
            {
                ForgeEventFactory.onPlayerDestroyItem(player, torchStack);
                player.inventory.mainInventory[i] = null;
            }
            if (result)
            {
                return true;
            }
        }
        return false;
    }
}
