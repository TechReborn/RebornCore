package reborncore.common.fluid;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Lazy;
import reborncore.RebornCoreClient;
import reborncore.modloader.events.client.SpriteRegistryEvent;

import java.util.stream.Stream;

public class RebornFluidRenderManager implements SpriteRegistryEvent {

	public static void setupClient(){
		RebornFluidRenderManager rebornFluidRenderManager = new RebornFluidRenderManager();
		SpriteRegistryEvent.BLOCK_ATLAS.register(rebornFluidRenderManager);
		RebornFluidManager.getFluidStream().forEach(RebornFluidRenderManager::setupFluidRenderer);
	}

	private static void setupFluidRenderer(RebornFluid fluid){
		//Done lazy as we want to ensure we get the sprite at the correct time, but also dont want to be making these calls every time its required.
		Lazy<Sprite[]> sprites = new Lazy<>(() -> {
			SpriteAtlasTexture spriteAtlas = MinecraftClient.getInstance().getSpriteAtlas();
			FluidSettings fluidSettings = fluid.getFluidSettings();
			return new Sprite[]{spriteAtlas.getSprite(fluidSettings.getStillTexture()), spriteAtlas.getSprite(fluidSettings.getFlowingTexture())};
		});

		RebornCoreClient.hooks.getFluidRenderHandlerRegistry().register(fluid, (extendedBlockView, blockPos, fluidState) -> sprites.get());
	}

	@Override
	public void registerSprites(SpriteAtlasTexture spriteAtlasTexture, Registry registry) {
		Stream.concat(
			RebornFluidManager.getFluidStream().map(rebornFluid -> rebornFluid.getFluidSettings().getFlowingTexture()),
			RebornFluidManager.getFluidStream().map(rebornFluid -> rebornFluid.getFluidSettings().getStillTexture())
		).forEach(registry::register);
	}
}
