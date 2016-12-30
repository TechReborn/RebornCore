package reborncore.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import reborncore.asm.mixin.Mixin;
import reborncore.asm.mixin.Remap;
import reborncore.asm.mixin.Rewrite;

@Mixin(target = "net.minecraft.entity.projectile.EntityArrow")
public abstract class MixinArrow extends Entity {

	@Remap(SRG = "field_70254_i")
	protected boolean inGround;
	@Remap(SRG = "field_70250_c")
	public Entity shootingEntity;

	public MixinArrow(World worldIn) {
		super(worldIn);
	}

	@Rewrite(target = "onUpdate", location = "END", targetSRG = "func_70071_h_")
	public void onUpdate(){
		if(shootingEntity instanceof EntityPlayer && inGround){
			worldObj.createExplosion(null, posX, posY, posZ,  5F, true);
			setDead();
			return;
		}
	}
}
