package teamreborn.reborncore.api.registry.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
public @interface BlockRegistry
{

	String param() default "";

	String itemBlock() default "";
}
