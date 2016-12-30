package reborncore.mixins;

import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.asm.mixin.Mixin;
import reborncore.asm.mixin.Rewrite;

import java.util.Random;

@Mixin(target = "net.minecraft.block.BlockReed")
public class MixinReed {

	@Rewrite(target = "updateTick", targetSRG = "func_180650_b")
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 10F, true);
	}
}
