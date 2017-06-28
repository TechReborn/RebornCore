package reborncore.mixin.implementations.asmInjector;

import javassist.ByteArrayClassPath;
import javassist.LoaderClassPath;
import reborncore.mixin.MixinManager;
import reborncore.mixin.implementations.prebaker.MixinPrebaker;
import reborncore.mixin.transformer.MixinTransformer;

/**
 * Used by asm injector to load mixins into a jar at build time
 */
public class ASMInjectorSetupClass {

	public static void main() {
		MixinManager.logger = System.out::println;
		MixinManager.logger.log("Loading mixin manager");
		MixinManager.logger.log("Using dummy remapper");
		MixinManager.mixinRemaper = new MixinPrebaker.DummyRemapper();
		MixinManager.load();
	}
	public static void setClassLoader(ClassLoader classLoader) {
		System.out.println("Setting logger");
		MixinManager.logger = System.out::println;
		MixinManager.logger.log("Setting class loader");
		MixinTransformer.cp.appendClassPath(new LoaderClassPath(classLoader));
	}

	public static void loadClass(String name, byte[] bytes) {
		MixinTransformer.cp.insertClassPath(new ByteArrayClassPath(name, bytes));
		MixinTransformer.preLoadedClasses.add(name);
	}

}
