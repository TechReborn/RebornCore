package reborncore.mixin.common;

import net.minecraft.container.CraftingResultSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reborncore.api.events.ItemCraftCallback;
import reborncore.common.recipes.ExtendedRecipeRemainder;

@Mixin(CraftingResultSlot.class)
public abstract class MixinCraftingResultSlot {

	@Shadow
	@Final
	private CraftingInventory craftingInv;

	@Shadow @Final private PlayerEntity player;

	@ModifyVariable(method = "onTakeItem", at = @At(value = "INVOKE"), index = 3)
	private DefaultedList<ItemStack> defaultedList(DefaultedList<ItemStack> list) {
		for (int i = 0; i < craftingInv.getInvSize(); i++) {
			ItemStack invStack = craftingInv.getInvStack(i);
			if (invStack.getItem() instanceof ExtendedRecipeRemainder) {
				ItemStack remainder = ((ExtendedRecipeRemainder) invStack.getItem()).getRemainderStack(invStack.copy());
				if (!remainder.isEmpty()) {
					list.set(i, remainder);
				}
			}
		}
		return list;
	}

	@Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;onCrafted(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V", shift = At.Shift.AFTER))
	private void onCrafted(ItemStack itemStack, CallbackInfo info){
		ItemCraftCallback.EVENT.invoker().onCraft(itemStack, player);
	}

}