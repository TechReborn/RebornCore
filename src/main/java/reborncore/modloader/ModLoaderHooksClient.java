package reborncore.modloader;

import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.modloader.events.client.RebornFluidRenderHandlerRegistry;

import java.util.function.Function;

public interface ModLoaderHooksClient {

	Sprite getFluidSprite(Fluid fluid, World world, BlockPos pos);

	int getFluidColor(Fluid fluid, World world, BlockPos pos);

	void extendItemDynamicRenderer(Function<ItemDynamicRenderer, ItemDynamicRenderer> function);

	float getCameraY();

	RebornFluidRenderHandlerRegistry getFluidRenderHandlerRegistry();

}
