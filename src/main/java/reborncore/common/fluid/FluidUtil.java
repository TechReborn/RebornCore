package reborncore.common.fluid;

import io.github.prospector.silk.fluid.FluidInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import reborncore.common.util.Tank;


public class FluidUtil {

	@Deprecated
	public static FluidInstance getFluidHandler(ItemStack stack) {
		return null;
	}

	@Deprecated
	public static boolean interactWithFluidHandler(PlayerEntity playerIn, Hand hand, Tank tank) {
		return false;
	}

	@Deprecated
	public static ItemStack getFilledBucket(FluidInstance stack) {
		return null;
	}

	public static String getFluidName(FluidInstance fluidInstance){
		return getFluidName(fluidInstance.getFluid());
	}

	public static String getFluidName(Fluid fluid){
		return "";
	}
}
