package teamreborn.reborncore.api.registry;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IRegistryFactory
{

	Class<? extends Annotation> getAnnotation();

	public default void handleField(Field field)
	{
	}

	public default void handleMethod(Method method)
	{
	}

	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.TYPE)
	public @interface RegistryFactory
	{

	}

}
