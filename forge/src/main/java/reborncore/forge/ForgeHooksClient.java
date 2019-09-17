package reborncore.forge;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.modloader.ModLoaderHooksClient;
import reborncore.modloader.events.client.RebornFluidRenderHandlerRegistry;

import java.util.function.Function;

public class ForgeHooksClient implements ModLoaderHooksClient {

	@Override
	public TextureAtlasSprite getFluidSprite(Fluid fluid, World world, BlockPos blockPos) {
		return null;
	}

	@Override
	public int getFluidColor(Fluid fluid, World world, BlockPos blockPos) {
		return 0;
	}

	@Override
	public void extendItemDynamicRenderer(Function<ItemStackTileEntityRenderer, ItemStackTileEntityRenderer> function) {
		ItemStackTileEntityRenderer.instance = function.apply(ItemStackTileEntityRenderer.instance);
	}

	@Override
	public float getCameraY() {
		return 0;
	}

	@Override
	public RebornFluidRenderHandlerRegistry getFluidRenderHandlerRegistry() {
		return null;
	}
}
