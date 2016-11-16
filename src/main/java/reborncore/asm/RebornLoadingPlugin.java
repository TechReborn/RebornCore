package reborncore.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

/**
 * Created by modmuss50 on 16/11/16.
 */
//-Dfml.coreMods.load=reborncore.asm.RebornLoadingPlugin
public class RebornLoadingPlugin implements IFMLLoadingPlugin {

	public RebornLoadingPlugin() {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.reborncore.json");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "reborncore.asm.ClassTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
