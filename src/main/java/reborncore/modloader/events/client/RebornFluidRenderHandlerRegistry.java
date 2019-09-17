package reborncore.modloader.events.client;

import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

public interface RebornFluidRenderHandlerRegistry {
	FluidRenderHandler get(Fluid var1);

    void register(Fluid fluid, FluidRenderHandler fluidRenderHandler);

	interface FluidRenderHandler {
		Sprite[] getFluidSprites(ExtendedBlockView var1, BlockPos var2, FluidState var3);

		default int getFluidColor(ExtendedBlockView view, BlockPos pos, FluidState state) {
			return -1;
		}
	}

}
