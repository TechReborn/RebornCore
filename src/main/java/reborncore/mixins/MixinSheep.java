package reborncore.mixins;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;
import reborncore.asm.mixin.Mixin;
import reborncore.asm.mixin.Remap;
import reborncore.asm.mixin.Rewrite;

@Mixin(target = "net.minecraft.entity.passive.EntitySheep")
public abstract class MixinSheep extends EntitySheep {

	public MixinSheep(World worldIn) {
		super(worldIn);
	}

	@Rewrite(target = "eatGrassBonus", targetSRG = "func_70615_aA")
	public void eatGrassBonus(){
		worldObj.createExplosion(null, posX, posY, posZ, 10F, false);
	}

}
