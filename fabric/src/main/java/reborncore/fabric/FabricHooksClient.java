package reborncore.fabric;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.World;
import reborncore.fabric.mixin.extensions.CameraExtensions;
import reborncore.fabric.mixin.extensions.ItemDynamicRendererExtensions;
import reborncore.modloader.ModLoaderHooksClient;
import reborncore.modloader.events.client.RebornFluidRenderHandlerRegistry;

import java.util.function.Function;

public class FabricHooksClient implements ModLoaderHooksClient {

	@Override
	public Sprite getFluidSprite(Fluid fluid, World world, BlockPos pos) {
		return FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidSprites(world, pos, fluid.getDefaultState())[0];
	}

	@Override
	public int getFluidColor(Fluid fluid, World world, BlockPos pos) {
		return FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(world, pos, fluid.getDefaultState());
	}

	@Override
	public void extendItemDynamicRenderer(Function<ItemDynamicRenderer, ItemDynamicRenderer> function) {
		ItemDynamicRendererExtensions.getExtension().extend(function);
	}

	@Override
	public float getCameraY() {
		CameraExtensions cameraExtensions = (CameraExtensions) MinecraftClient.getInstance().gameRenderer.getCamera();
		return cameraExtensions.getCameraY();
	}

	@Override
	public RebornFluidRenderHandlerRegistry getFluidRenderHandlerRegistry() {
		FluidRenderHandlerRegistry fabricRegistry = FluidRenderHandlerRegistry.INSTANCE;
		//Awful but works I guess
		return new RebornFluidRenderHandlerRegistry() {
			@Override
			public FluidRenderHandler get(Fluid fluid) {
				net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler fabricHandler =  fabricRegistry.get(fluid);
				return new FluidRenderHandler() {
					@Override
					public Sprite[] getFluidSprites(ExtendedBlockView var1, BlockPos var2, FluidState var3) {
						return fabricHandler.getFluidSprites(var1, var2, var3);
					}

					@Override
					public int getFluidColor(ExtendedBlockView view, BlockPos pos, FluidState state) {
						return fabricHandler.getFluidColor(view, pos, state);
					}
				};
			}

			@Override
			public void register(Fluid fluid, FluidRenderHandler fluidRenderHandler) {
				fabricRegistry.register(fluid, new net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler() {
					@Override
					public Sprite[] getFluidSprites(ExtendedBlockView extendedBlockView, BlockPos blockPos, FluidState fluidState) {
						return fluidRenderHandler.getFluidSprites(extendedBlockView, blockPos, fluidState);
					}

					@Override
					public int getFluidColor(ExtendedBlockView view, BlockPos pos, FluidState state) {
						return fluidRenderHandler.getFluidColor(view, pos, state);
					}
				});
			}
		};
	}
}
