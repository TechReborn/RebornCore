package reborncore.mixin.implementations.asmInjector;

import org.apache.logging.log4j.LogManager;
import reborncore.mixin.MixinManager;
import reborncore.mixin.implementations.prebaker.MixinPrebaker;

/**
 * Used by asm injector to load mixins into a jar at build time
 */
public class ASMInjectorSetupClass {

	public static void main(){
		MixinManager.logger = LogManager.getLogger();
		MixinManager.logger.info("Loading mixin manager");
		MixinManager.logger.info("Using dummy remapper");
		MixinManager.mixinRemaper = new MixinPrebaker.DummyRemapper();
		MixinManager.load();
	}

}
