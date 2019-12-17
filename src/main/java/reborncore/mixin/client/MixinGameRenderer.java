package reborncore.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import reborncore.api.items.ArmorFovHandler;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Shadow @Final private MinecraftClient client;

	@Redirect(method = "updateMovementFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSpeed()F"))
	private float updateMovementFovMultiplier(AbstractClientPlayerEntity playerEntity) {
		float playerSpeed = playerEntity.getSpeed();
		for (ItemStack stack : playerEntity.getArmorItems()) {
			if (stack.getItem() instanceof ArmorFovHandler) {
				playerSpeed = ((ArmorFovHandler) stack.getItem()).changeFov(playerSpeed, stack, client.player);
			}
		}
		return playerSpeed;
	}

}
