package reborncore.mixin.transformer;

import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.mixin.api.Rewrite;

import java.util.Optional;

/**
 * Used to remap mixins to the target.
 */
public interface IMixinRemap {

	void remap(CtClass mixinClass, ClassPool classPool);

	//TODO get rid of this, and allow the whole thing to be remapped in the remap function

	/**
	 * Left is just the method name
	 * Right is the method name + signature, in SRG format.
	 *
	 * @return
	 */
	Optional<Pair<String, String>> getFullTargetName(Rewrite rewriteAnn, String name);
}
