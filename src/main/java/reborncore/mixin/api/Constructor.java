package reborncore.mixin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is used to inject things into a constructor. Please note this will be added after all instructions in the target constructor.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Constructor {

	/**
	 * The signature of the target constructor.
	 *
	 * @return
	 */
	String signature();

}
