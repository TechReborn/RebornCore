package reborncore.mixin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is applied to methords to allow them to be repalced, or injected into.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Rewrite {

	/**
	 * The name of the target method.
	 *
	 * @return
	 */
	String target();

	/**
	 * The srg name of the target.
	 *
	 * @return
	 */
	String targetSRG() default "";

	/**
	 * This is used to specily how the mixin transformer should apply the mixin method to the target method.
	 *
	 * @return The behavior
	 */
	Behavior behavior() default Behavior.START;

	ReturnBehavior returnBehavor() default ReturnBehavior.NONE;

	boolean isStatic() default false;

	/**
	 * Start: Gets applied before all instructions in the target method.
	 * End: Get added after all instructions in the method.
	 * Replace: The whole method body is set to the mixins.
	 */
	enum Behavior {
		START, END, REPLACE
	}

	enum ReturnBehavior {
		NONE, OBJECT_NONE_NULL, BOOL_TRUE
	}

}
