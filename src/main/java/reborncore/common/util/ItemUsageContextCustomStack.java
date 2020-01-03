package reborncore.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemUsageContextCustomStack extends ItemUsageContext {

	public ItemUsageContextCustomStack(World world, @Nullable PlayerEntity player, Hand hand, ItemStack stack, BlockHitResult hit) {
		super(world, player, hand, stack, hit);
	}
}
