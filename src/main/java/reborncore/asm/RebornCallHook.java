package reborncore.asm;

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import reborncore.asm.mixin.MixinManager;

import java.util.Map;

/**
 * Created by Mark on 30/12/2016.
 */
public class RebornCallHook implements IFMLCallHook {
	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public Void call() throws Exception {
		MixinManager.loadMixinData();
		return null;
	}
}
