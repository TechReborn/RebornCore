package reborncore.dev;

import net.minecraft.item.Item;
import reborncore.mixin.api.Mixin;
import reborncore.mixin.api.Rewrite;

@Mixin(target = "net.minecraft.item.ItemStack")
public class MixinItemStack {

	@Rewrite(target = "setItem", behavior = Rewrite.Behavior.START)
	public void setItem(Item newItem) {
		EmptyItemStackChecker.setItem(newItem);
	}
}
