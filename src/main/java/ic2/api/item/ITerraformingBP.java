package ic2.api.item;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITerraformingBP
{
	int getConsume();

	int getRange();

	boolean terraform(World p0, BlockPos pos);
}
