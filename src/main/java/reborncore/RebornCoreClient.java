package reborncore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import reborncore.client.IconSupplier;
import reborncore.client.shields.RebornItemStackRenderer;
import reborncore.common.fluid.RebornFluidRenderManager;
import reborncore.common.network.ClientBoundPacketHandlers;

public class RebornCoreClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		RebornFluidRenderManager.setupClient();
		ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register(IconSupplier::registerSprites);
		RebornItemStackRenderer.setup();
		ClientBoundPacketHandlers.init();
	}
}
