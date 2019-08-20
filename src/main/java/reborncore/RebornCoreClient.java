package reborncore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import reborncore.client.IconSupplier;
import reborncore.client.multiblock.MultiblockRenderEvent;
import reborncore.client.shields.RebornItemStackRenderer;
import reborncore.common.fluid.RebornFluidRenderManager;
import reborncore.common.network.ClientBoundPacketHandlers;

public class RebornCoreClient implements ClientModInitializer {

	public static MultiblockRenderEvent multiblockRenderEvent = new MultiblockRenderEvent();

	@Override
	public void onInitializeClient() {
		RebornFluidRenderManager.setupClient();
		AttackBlockCallback.EVENT.register(multiblockRenderEvent);
		ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register(IconSupplier::registerSprites);
		RebornItemStackRenderer.setup();
		ClientBoundPacketHandlers.init();
	}
}
