package reborncore.mixin.client;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import reborncore.api.events.ItemTooltipCallback;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ItemStack.class)
public class MixinItemStack {

	@Inject(method = "getTooltipText", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void getTooltipText(@Nullable PlayerEntity playerEntity_1, TooltipContext tooltipContext_1, CallbackInfoReturnable<List<Component>> info, List<Component> components){
		ItemTooltipCallback.EVENT.invoker().getTooltip((ItemStack) (Object)this, tooltipContext_1, components);
	}

}
