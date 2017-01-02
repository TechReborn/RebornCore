package reborncore.mixin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is used on a field to remap it to srg names before it is applied at runtime.-
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Remap {

	/**
	 * @return the srg name of the field that this is being applied to.
	 */
	String SRG();

}
