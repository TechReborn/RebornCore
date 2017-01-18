package reborncore.mixin.implementations.forge;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import net.minecraftforge.fml.common.Loader;
import reborncore.mixin.MixinManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import reborncore.mixin.json.MixinTargetData;
import reborncore.mixin.transformer.MixinTransformer;

import java.util.Map;

//

/**
 * To run this in dev you need to add the following to the *VM* Options in the run config
 *
 *          -Dfml.coreMods.load=reborncore.mixin.implementations.forge.MixinForgeLoadingCore
 *
 */
@IFMLLoadingPlugin.MCVersion("1.11.2")
@IFMLLoadingPlugin.Name("RebornCoreASM")
public class MixinForgeLoadingCore implements IFMLLoadingPlugin {

	//True when using SRG names
	public static boolean runtimeDeobfuscationEnabled = true;

	public static boolean mixinsLoaded = false;

	public MixinForgeLoadingCore() throws NotFoundException, ClassNotFoundException {
		//Adds the launchwrappers class loader to java assist, this allows mixins to be loaded form the mod folder.
		MixinTransformer.cp.appendClassPath(new LoaderClassPath(Launch.classLoader));
		MixinTransformer.cp.appendClassPath(new LoaderClassPath(Loader.instance().getModClassLoader()));
		MixinManager.mixinRemaper = new ForgeRemapper();
		MixinManager.logger = FMLLog.getLogger();//TODO don't use the FML logger?
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "reborncore.mixin.transformer.MixinTransformer" };
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
		runtimeDeobfuscationEnabled = (boolean) data.get("runtimeDeobfuscationEnabled");
		mixinsLoaded = true;
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
