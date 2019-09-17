package reborncore.fabric.mixin.common;

import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.BaseFluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import reborncore.fabric.mixin.extensions.FluidBlockExtensions;

@Mixin(FluidBlock.class)
public class MixinFluidBlock implements FluidBlockExtensions {

	@Shadow @Final protected BaseFluid fluid;

	@Override
	public BaseFluid getFluid() {
		return fluid;
	}
}
