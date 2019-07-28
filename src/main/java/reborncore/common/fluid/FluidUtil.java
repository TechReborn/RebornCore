package reborncore.common.fluid;

import io.github.prospector.silk.fluid.FluidInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
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

	public static String getFluidName(@NonNull FluidInstance fluidInstance){
		return getFluidName(fluidInstance.getFluid());
	}

	public static String getFluidName(@NonNull Fluid fluid){
		return StringUtils.capitalize(Registry.FLUID.getId(fluid).getPath());
	}
}
