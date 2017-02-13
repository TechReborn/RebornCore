package reborncore.mixin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is used on methods or fields to state that they should be injected as is into the target class. Take note that this will fail if it tries to overwrite an existing one.
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

	/**
	 * When true the method will be renamed the same as @rewrite does it
	 *
	 * @return
	 */
	boolean rename() default false;

}
