package reborncore.fabric.mixin.client;

import net.minecraft.client.render.item.ItemDynamicRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import reborncore.fabric.mixin.extensions.ItemDynamicRendererExtensions;

import java.util.function.Function;

@Mixin(ItemDynamicRenderer.class)
public class MixinItemDynamicRenderer implements ItemDynamicRendererExtensions {

	@Shadow @Final @Mutable
	public static ItemDynamicRenderer INSTANCE;

	@Override
	public void extend(Function<ItemDynamicRenderer, ItemDynamicRenderer> function) {
		INSTANCE = function.apply(INSTANCE);
	}
}
