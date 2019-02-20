/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.registration;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import reborncore.RebornCore;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to handle annoations that are used to register things at runtime.
 */
public interface IRegistryFactory {

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
	public default void handleField(String modID, Field field) {
	}

	/**
	 * Called when the specificyed annotation is found on a method, and the target list contains RegistryTarget.MEHTOD
	 *
	 * @param method the method
	 */
	public default void handleMethod(String modID, Method method) {
	}

	/**
	 * Called when the specificyed annotation is found on a class, and the target list contains RegistryTarget.CLASS
	 *
	 * @param clazz the class
	 */
	public default void handleClass(String modID, Class clazz) {
	}

	/**
	 * Called once all of the handle methods have finished
	 */
	public default void factoryComplete() {

	}

	/**
	 * This is used to determine what type of targetes the factory should target. Try and only list the targets that are of use to keep load times down.
	 *
	 * The deafult is for all
	 *
	 * @return A list of RegistryTarget's that this factory will atempt to behavior
	 */
	public default List<RegistryTarget> getTargets() {
		return Arrays.asList(RegistryTarget.CLASS, RegistryTarget.MEHTOD, RegistryTarget.FIELD);
	}

	/**
	 * This annoation needs to be applied to to the class implimenting IRegistryFactory so Reborncore can register it.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RegistryFactory {

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
		String modID() default RebornCore.MOD_ID;
	}

	default LoadStage getProcessSate() {
		return LoadStage.CONSTRUCTION;
	}

}
