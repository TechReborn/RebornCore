package reborncore.fabric.mixin.extensions;

import net.minecraft.client.render.item.ItemDynamicRenderer;

import java.util.function.Function;

public interface ItemDynamicRendererExtensions {

	static ItemDynamicRendererExtensions getExtension(){
		return (ItemDynamicRendererExtensions) ItemDynamicRenderer.INSTANCE;
	}

	void extend(Function<ItemDynamicRenderer, ItemDynamicRenderer> function);

}
