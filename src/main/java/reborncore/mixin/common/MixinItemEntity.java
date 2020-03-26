package reborncore.mixin.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reborncore.common.misc.RebornCoreTags;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
	@Shadow
	public abstract ItemStack getStack();

	public MixinItemEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void tick(CallbackInfo info) {
		if (!world.isClient && isTouchingWater() && !getStack().isEmpty()) {
			if (getStack().getItem().isIn(RebornCoreTags.WATER_EXPLOSION_ITEM)) {
				world.createExplosion(this, getX(), getY(), getZ(), 2F, Explosion.DestructionType.BREAK);
				this.remove();
			}
		}
	}

}
