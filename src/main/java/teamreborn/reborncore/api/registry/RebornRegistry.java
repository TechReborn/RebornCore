package teamreborn.reborncore.api.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This anoation gets applied to any class that contains
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface RebornRegistry
{

	/**
	 * The mod id that should be active when the annotion is being proccessed.
	 *
	 * @return mod id
	 */
	public String value();

}
