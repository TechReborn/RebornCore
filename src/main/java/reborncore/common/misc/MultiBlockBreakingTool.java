package reborncore.common.misc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import java.util.Set;

public interface MultiBlockBreakingTool {

	Set<BlockPos> getBlocksToBreak(ItemStack stack, World worldIn, BlockPos pos, @Nullable LivingEntity entityLiving);
}
