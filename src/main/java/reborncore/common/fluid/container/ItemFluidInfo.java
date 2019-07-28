package reborncore.common.fluid.container;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

public interface ItemFluidInfo {

	ItemStack getEmpty();

	ItemStack getFull(Fluid fluid);

	Fluid getFluid(ItemStack itemStack);

}
