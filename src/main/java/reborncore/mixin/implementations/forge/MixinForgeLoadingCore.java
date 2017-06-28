/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.mixin.implementations.forge;

import javassist.LoaderClassPath;
import javassist.NotFoundException;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import reborncore.mixin.MixinManager;
import reborncore.mixin.transformer.MixinTransformer;

import java.util.Map;

//

/**
 * To run this in dev you need to add the following to the *VM* Options in the run config
 *
 * -Dfml.coreMods.load=reborncore.mixin.implementations.forge.MixinForgeLoadingCore
 */
@IFMLLoadingPlugin.MCVersion("1.12")
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
		MixinManager.load();
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
