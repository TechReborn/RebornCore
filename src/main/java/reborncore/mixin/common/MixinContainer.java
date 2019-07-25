package reborncore.mixin.common;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import reborncore.mixin.extensions.ContainerExtensions;

import java.util.List;

@Mixin(Container.class)
public class MixinContainer implements ContainerExtensions {

	@Shadow @Final private List<ContainerListener> listeners;

	@Override
	public List<ContainerListener> getListeners() {
		return listeners;
	}

}
