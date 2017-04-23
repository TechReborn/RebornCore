package teamreborn.reborncore.api.registry.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FluidRegistry {

	String name() default "";

	String path() default "blocks/fluids/";

	String material() default "water";

	int density() default 1000;

	int viscosity() default 1000;

	int luminosity() default 0;

	int temperature() default 295;

	boolean gaseous() default false;
}
