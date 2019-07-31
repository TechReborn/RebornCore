package reborncore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import reborncore.client.multiblock.MultiblockRenderEvent;
import reborncore.client.shields.RebornItemStackRenderer;
import reborncore.common.fluid.RebornFluidRenderManager;

public class RebornCoreClient implements ClientModInitializer {

	public static MultiblockRenderEvent multiblockRenderEvent = new MultiblockRenderEvent();

	@Override
	public void onInitializeClient() {
		RebornFluidRenderManager.setupClient();
		AttackBlockCallback.EVENT.register(multiblockRenderEvent);
		RebornItemStackRenderer.setup();
	}
}
