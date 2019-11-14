package reborncore.mixin.client;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import reborncore.mixin.extensions.ItemDynamicRendererExtensions;

import java.util.function.Function;

@Mixin(BuiltinModelItemRenderer.class)
public class MixinItemDynamicRenderer implements ItemDynamicRendererExtensions {

	@Shadow @Final @Mutable
	public static BuiltinModelItemRenderer INSTANCE;

	@Override
	public void extend(Function<BuiltinModelItemRenderer, BuiltinModelItemRenderer> function) {
		INSTANCE = function.apply(INSTANCE);
	}
}
