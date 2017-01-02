package reborncore.mixin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is applied to a mixin class, to specify that it should be used as a mixin.
 * Try not to refinance mixins from other classes, and they are only used for reference.
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.TYPE)
public @interface Mixin {

	/**
	 * The class that the mixin should be applied to.
	 *
	 * E.g: "package.something.ClassName"
	 *
	 * @return the class name;
	 */
	String target();

}
