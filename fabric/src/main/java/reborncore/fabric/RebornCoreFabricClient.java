package reborncore.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import reborncore.RebornCoreClient;
import reborncore.modloader.events.client.SpriteRegistryEvent;

public class RebornCoreFabricClient implements ClientModInitializer {

	public RebornCoreFabricClient() {
		RebornCoreClient.hooks = new FabricHooksClient();
	}

	@Override
	public void onInitializeClient() {
		RebornCoreClient.INSTANCE.onInitializeClient();

		ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> SpriteRegistryEvent.BLOCK_ATLAS.post(spriteRegistryEvent -> spriteRegistryEvent.registerSprites(atlasTexture, new SpriteRegistryEvent.Registry() {
			@Override
			public void register(Identifier id) {
				registry.register(id);
			}

			@Override
			public void register(Sprite sprite) {
				registry.register(sprite);
			}
		})));
	}
}
