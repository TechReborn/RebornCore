package reborncore.mixin.common;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reborncore.api.items.ArmorRemoveHandler;
import reborncore.api.items.ArmorTickable;
import reborncore.common.util.ItemUtils;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

	@Shadow
	public abstract Iterable<ItemStack> getArmorItems();

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

	private DefaultedList<ItemStack> reborncore_armorcache = DefaultedList.ofSize(4, ItemStack.EMPTY);

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo info) {
		int i = 0;
		for (ItemStack stack : getArmorItems()) {
			ItemStack cachedStack = reborncore_armorcache.get(i);
			if (!ItemUtils.isItemEqual(cachedStack, stack, false, false)) {
				if (cachedStack.getItem() instanceof ArmorRemoveHandler) {
					((ArmorRemoveHandler) cachedStack.getItem()).onRemoved((PlayerEntity) (Object) this);
				}
				reborncore_armorcache.set(i, stack.copy());
			}
			i++;

			if (!stack.isEmpty() && stack.getItem() instanceof ArmorTickable) {
				((ArmorTickable) stack.getItem()).tickArmor(stack, (PlayerEntity) (Object) this);
			}
		}
	}
}
