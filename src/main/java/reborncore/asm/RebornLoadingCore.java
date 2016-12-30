package reborncore.asm;

import javassist.NotFoundException;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import reborncore.RebornCore;
import reborncore.asm.mixin.MixinManager;
import reborncore.common.util.LogHelper;

import java.util.Map;

//-Dfml.coreMods.load=reborncore.asm.RebornLoadingCore
@IFMLLoadingPlugin.MCVersion("1.10.2")
@IFMLLoadingPlugin.Name("RebornCoreASM")
public class RebornLoadingCore implements IFMLLoadingPlugin {

	public static LogHelper logHelper;
	//True when using SRG names
	public static boolean runtimeDeobfuscationEnabled = true;

	public RebornLoadingCore() throws NotFoundException, ClassNotFoundException {
		logHelper = new LogHelper();
		MixinManager.registerMixin("reborncore.mixins.MixinReed", "net.minecraft.block.BlockReed");
		MixinManager.registerMixin("reborncore.mixins.MixinChestTile", "net.minecraft.tileentity.TileEntityChest");
		MixinManager.registerMixin("reborncore.mixins.MixinSheep", "net.minecraft.entity.passive.EntitySheep");
		MixinManager.registerMixin("reborncore.mixins.MixinArrow", "net.minecraft.entity.projectile.EntityArrow");
		MixinManager.registerMixin("reborncore.mixins.MixinSnowBall", "net.minecraft.entity.projectile.EntitySnowball");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"reborncore.asm.mixin.MixinTransfomer"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return "reborncore.asm.RebornCallHook";
	}

	@Override
	public void injectData(Map<String, Object> data) {
		runtimeDeobfuscationEnabled = (boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
