package reborncore.mixins;

import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import reborncore.asm.mixin.Mixin;
import reborncore.asm.mixin.Rewrite;

@Mixin(target = "net.minecraft.entity.projectile.EntitySnowball")
public abstract class MixinSnowBall extends EntitySnowball {

	public MixinSnowBall(World worldIn) {
		super(worldIn);
	}

	@Rewrite(target = "onImpact", targetSRG = "func_70184_a")
	protected void onImpact(RayTraceResult result){
		for(EnumFacing dir : EnumFacing.VALUES){
			worldObj.setBlockState(new BlockPos(posX, posY, posZ).offset(dir), Blocks.SNOW.getDefaultState());
		}
		setDead();
	}
}
