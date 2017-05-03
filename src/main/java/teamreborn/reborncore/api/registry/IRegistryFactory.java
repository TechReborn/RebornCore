package teamreborn.reborncore.api.registry;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import teamreborn.reborncore.RCConstants;
import teamreborn.reborncore.api.ExecutionSide;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to handle annoations that are used to register things at runtime.
 */
public interface IRegistryFactory
{

	/**
	 * This is used to determine what class this factory should handle. Duplicates with other factorys are ok.
	 *
	 * @return The class of the annoation:
	 */
	Class<? extends Annotation> getAnnotation();

	/**
	 * Called when the specificyed annotation is found on a field, and the target list contains RegistryTarget.FIELD
	 *
	 * @param field the method
	 */
	public default void handleField(Field field)
	{
	}

	/**
	 * Called when the specificyed annotation is found on a method, and the target list contains RegistryTarget.MEHTOD
	 *
	 * @param method the method
	 */
	public default void handleMethod(Method method)
	{
	}

	/**
	 * Called when the specificyed annotation is found on a class, and the target list contains RegistryTarget.CLASS
	 *
	 * @param clazz the class
	 */
	public default void handleClass(Class clazz)
	{
	}

	/**
	 * This is used to determine what type of targetes the factory should target. Try and only list the targets that are of use to keep load times down.
	 *
	 * The deafult is for all
	 *
	 * @return A list of RegistryTarget's that this factory will atempt to process
	 */
	public default List<RegistryTarget> getTargets()
	{
		return Arrays.asList(RegistryTarget.CLASS, RegistryTarget.MEHTOD, RegistryTarget.FIELD);
	}


	public default Class<? extends FMLStateEvent> getProccessEvent() {
		return FMLPreInitializationEvent.class;
	}

	/**
	 * This annoation needs to be applied to to the class implimenting IRegistryFactory so Reborncore can register it.
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.TYPE)
	public @interface RegistryFactory
	{

		/**
		 * The side that this registry factyory should run on
		 *
		 * @return a side
		 */
		ExecutionSide side() default ExecutionSide.COMMON;

		/**
		 * A mod id that must be loaded for the registry factory to be ran
		 *
		 * @return a mod id
		 */
		String modID() default RCConstants.MOD_ID;
	}

}
