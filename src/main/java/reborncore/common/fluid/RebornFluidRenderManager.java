package reborncore.common.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Lazy;
import reborncore.client.RenderUtil;

import java.util.stream.Stream;

public class RebornFluidRenderManager implements ClientSpriteRegistryCallback {

	public static void setupClient(){
		RebornFluidRenderManager rebornFluidRenderManager = new RebornFluidRenderManager();
		ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register(rebornFluidRenderManager);
		RebornFluidManager.getFluidStream().forEach(RebornFluidRenderManager::setupFluidRenderer);
	}

	private static void setupFluidRenderer(RebornFluid fluid){
		//Done lazy as we want to ensure we get the sprite at the correct time, but also dont want to be making these calls every time its required.
		Lazy<Sprite[]> sprites = new Lazy<>(() -> {
			FluidSettings fluidSettings = fluid.getFluidSettings();
			return new Sprite[]{RenderUtil.getSprite(fluidSettings.getStillTexture()), RenderUtil.getSprite(fluidSettings.getFlowingTexture())};
		});

		FluidRenderHandlerRegistry.INSTANCE.register(fluid, (extendedBlockView, blockPos, fluidState) -> sprites.get());
	}

	@Override
	public void registerSprites(SpriteAtlasTexture spriteAtlasTexture, Registry registry) {
		Stream.concat(
			RebornFluidManager.getFluidStream().map(rebornFluid -> rebornFluid.getFluidSettings().getFlowingTexture()),
			RebornFluidManager.getFluidStream().map(rebornFluid -> rebornFluid.getFluidSettings().getStillTexture())
		).forEach(registry::register);
	}
}
