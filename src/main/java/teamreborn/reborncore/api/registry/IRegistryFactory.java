package teamreborn.reborncore.api.registry;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IRegistryFactory {

	Class<? extends Annotation> getAnnotation();

	public default void handleField(Field field) {}

	public default void handleMethod(Method method) {}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RegistryFactory {

	}

}
