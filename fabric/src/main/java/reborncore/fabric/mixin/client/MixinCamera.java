package reborncore.fabric.mixin.client;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import reborncore.fabric.mixin.extensions.CameraExtensions;

@Mixin(Camera.class)
public class MixinCamera implements CameraExtensions {

	@Shadow private float cameraY;

	@Override
	public float getCameraY() {
		return cameraY;
	}
}
