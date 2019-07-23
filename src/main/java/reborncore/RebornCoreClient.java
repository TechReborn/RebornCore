package reborncore;

import net.fabricmc.api.ClientModInitializer;
import reborncore.common.fluid.RebornFluidRenderManager;

public class RebornCoreClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		RebornFluidRenderManager.setupClient();
	}
}
