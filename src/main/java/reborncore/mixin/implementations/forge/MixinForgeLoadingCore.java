package reborncore.mixin.implementations.forge;

import javassist.LoaderClassPath;
import javassist.NotFoundException;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import reborncore.dev.EmptyItemStackChecker;
import reborncore.mixin.MixinManager;
import reborncore.mixin.json.MixinTargetData;
import reborncore.mixin.transformer.MixinTransformer;

import java.io.File;
import java.util.Map;

//

/**
 * To run this in dev you need to add the following to the *VM* Options in the run config
 *
 *          -Dfml.coreMods.load=reborncore.mixin.implementations.forge.MixinForgeLoadingCore
 *
 */
@IFMLLoadingPlugin.MCVersion("1.10.2")
@IFMLLoadingPlugin.Name("RebornCoreASM")
public class MixinForgeLoadingCore implements IFMLLoadingPlugin {

	//True when using SRG names
	public static boolean runtimeDeobfuscationEnabled = true;

	public static boolean mixinsLoaded = false;

	public MixinForgeLoadingCore() throws NotFoundException, ClassNotFoundException {
		//Adds the launchwrappers class loader to java assist, this allows mixins to be loaded form the mod folder.
		MixinTransformer.cp.appendClassPath(new LoaderClassPath(Launch.classLoader));
		MixinManager.mixinRemaper = new ForgeRemapper();
		MixinManager.logger = FMLLog.getLogger();//TODO don't use the FML logger?
		MixinManager.registerMixin(new MixinTargetData("reborncore.client.mixin.MixinRenderItem", "net.minecraft.client.renderer.RenderItem"));

		//DEV ONLY enable with: -Dreborncore.nullItemChecking=true
		if(Boolean.parseBoolean(System.getProperty("reborncore.nullItemChecking", "false"))){
			MixinManager.logger.warn("Reborncore Dev Features enabled, things will break");
			MixinManager.registerMixin(new MixinTargetData("reborncore.dev.MixinItemStack", "net.minecraft.item.ItemStack"));
			EmptyItemStackChecker.exportFile = new File(new File("."), "nullItems.txt");
			if(EmptyItemStackChecker.exportFile.exists()){
				EmptyItemStackChecker.exportFile.delete();
			}
		}
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
