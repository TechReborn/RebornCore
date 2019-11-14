package reborncore.mixin.extensions;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;

import java.util.function.Function;

public interface ItemDynamicRendererExtensions {

	static ItemDynamicRendererExtensions getExtension(){
		return (ItemDynamicRendererExtensions) BuiltinModelItemRenderer.INSTANCE;
	}

	void extend(Function<BuiltinModelItemRenderer, BuiltinModelItemRenderer> function);

}
