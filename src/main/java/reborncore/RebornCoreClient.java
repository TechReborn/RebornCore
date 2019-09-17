package reborncore;

import net.minecraft.client.texture.SpriteAtlasTexture;
import reborncore.client.IconSupplier;
import reborncore.client.multiblock.MultiblockRenderEvent;
import reborncore.client.shields.RebornItemStackRenderer;
import reborncore.common.fluid.RebornFluidRenderManager;
import reborncore.common.network.ClientBoundPacketHandlers;
import reborncore.modloader.ModLoaderHooksClient;
import reborncore.modloader.events.AttackBlockEvent;
import reborncore.modloader.events.client.SpriteRegistryEvent;

public class RebornCoreClient {

	public static final RebornCoreClient INSTANCE = new RebornCoreClient();
	public static ModLoaderHooksClient hooks;

	public static MultiblockRenderEvent multiblockRenderEvent = new MultiblockRenderEvent();

	public void onInitializeClient() {
		RebornFluidRenderManager.setupClient();
		AttackBlockEvent.HANDLER.register(multiblockRenderEvent);
		SpriteRegistryEvent.BLOCK_ATLAS.register(IconSupplier::registerSprites);
		RebornItemStackRenderer.setup();
		ClientBoundPacketHandlers.init();
	}
}
