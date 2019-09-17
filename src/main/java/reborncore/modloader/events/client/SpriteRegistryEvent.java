package reborncore.modloader.events.client;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import reborncore.modloader.events.EventHandler;


public interface SpriteRegistryEvent {

	EventHandler<SpriteRegistryEvent> BLOCK_ATLAS = new EventHandler<>();

	void registerSprites(SpriteAtlasTexture atlasTexture, Registry registry);

	interface Registry {
		void register(Identifier id);
		void register(Sprite sprite);
	}

}
