package reborncore.mixin.common;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reborncore.api.blockentity.UnloadHandler;

import java.util.List;

@Mixin(World.class)
public class MixinWorld {

	@Shadow @Final protected List<BlockEntity> unloadedBlockEntities;

	@Inject(method = "tickBlockEntities", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = {"ldc=blockEntities"}))
	public void tickBlockEntities(CallbackInfo info) {
		if (!unloadedBlockEntities.isEmpty()) {
			for (BlockEntity blockEntity : unloadedBlockEntities) {
				if (blockEntity instanceof UnloadHandler) {
					((UnloadHandler) blockEntity).onUnload();
				}
			}
		}
	}
}
