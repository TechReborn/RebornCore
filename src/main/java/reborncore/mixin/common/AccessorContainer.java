package reborncore.mixin.common;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Container.class)
public interface AccessorContainer {

	@Accessor
	List<ContainerListener> getListeners();

}
