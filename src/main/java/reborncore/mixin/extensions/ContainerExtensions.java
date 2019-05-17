package reborncore.mixin.extensions;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;

import java.util.List;

public interface ContainerExtensions {

	static ContainerExtensions get(Container container){
		return (ContainerExtensions) container;
	}

	List<ContainerListener> getListeners();

}
