package reborncore.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reborncore.client.ClientChunkManager;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {

	@Inject(method = "render", at = @At("RETURN"))
	public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
		ClientChunkManager.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
	}
}
