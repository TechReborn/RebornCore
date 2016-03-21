package reborncore.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import reborncore.shields.RebornCoreShields;

import java.util.Map;

/**
 * Created by Mark on 21/03/2016.
 */
//-Dfml.coreMods.load=reborncore.asm.RebornASM
@IFMLLoadingPlugin.MCVersion("1.9")
public class RebornASM implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"reborncore.asm.RebornClassTransformer"};
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
