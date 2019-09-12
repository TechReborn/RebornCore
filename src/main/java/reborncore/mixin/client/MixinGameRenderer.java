package reborncore.mixin.client;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reborncore.RebornCoreClient;
import reborncore.client.ClientChunkManager;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Inject(method = "renderCenter", at = @At(value = "INVOKE", target = "net/minecraft/util/profiler/Profiler.swap(Ljava/lang/String;)V", ordinal = 15))
	private void renderCenter(float float_1, long long_1, CallbackInfo info) {
		RebornCoreClient.multiblockRenderEvent.onWorldRenderLast(float_1);

	}

	@Inject(method = "renderCenter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/debug/DebugRenderer;render(J)V", ordinal = 0))
	private void renderCenter_2(float float_1, long long_1, CallbackInfo info) {
		ClientChunkManager.render();
	}

}
