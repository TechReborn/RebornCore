package reborncore.common.itemblock;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import reborncore.common.achievement.ICraftAchievement;
import reborncore.common.achievement.IPickupAchievement;

public class ItemBlockBase extends ItemMultiTexture implements IPickupAchievement, ICraftAchievement {
    public ItemBlockBase(Block p_i45346_1_, Block p_i45346_2_,
                         String[] p_i45346_3_) {
        super(p_i45346_1_, p_i45346_2_, p_i45346_3_);
    }

    @Override
    public Achievement getAchievementOnCraft(ItemStack stack,
                                             EntityPlayer player, IInventory matrix) {
        return theBlock instanceof ICraftAchievement ? ((ICraftAchievement) theBlock)
                .getAchievementOnCraft(stack, player, matrix) : null;
    }

    @Override
    public Achievement getAchievementOnPickup(ItemStack stack,
                                              EntityPlayer player, EntityItem item) {
        return theBlock instanceof IPickupAchievement ? ((IPickupAchievement) theBlock)
                .getAchievementOnPickup(stack, player, item) : null;
    }

}
