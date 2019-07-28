package reborncore.mixin.common;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import reborncore.common.fluid.RebornFluidManager;
import reborncore.common.fluid.container.ItemFluidInfo;

@Mixin(BucketItem.class)
public class MixinBucketItem implements ItemFluidInfo {

	@Shadow @Final private Fluid fluid;

	@Override
	public ItemStack getEmpty() {
		return new ItemStack(Items.BUCKET);
	}

	@Override
	public ItemStack getFull(Fluid fluid) {
		BucketItem item = RebornFluidManager.getBucketMap().get(fluid);
		return new ItemStack(item);
	}

	@Override
	public Fluid getFluid(ItemStack itemStack) {
		return fluid;
	}
}
