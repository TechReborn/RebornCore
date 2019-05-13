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

import org.apache.commons.lang3.Validate;
import reborncore.RebornCore;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark on 26/02/2017.
 */
public class RegistrationManager {

	String modid;

	public RegistrationManager(String modid) {
		this.modid = modid;
		loadFactorys();
		init();
		load(LoadStage.CONSTRUCTION);
	}

	List<IRegistryFactory> factoryList = new ArrayList<>();
	List<Class> registryClasses = new ArrayList<>();

	private void init() {
		long start = System.currentTimeMillis();

		RebornCore.LOGGER.info("Pre loaded registries in " + (System.currentTimeMillis() - start) + "ms");
	}



	public void load(LoadStage event) {
		long start = System.currentTimeMillis();

		List<IRegistryFactory> factoryList = getFactorysForSate(event);
		if (!factoryList.isEmpty()) {
			for (Class clazz : registryClasses) {
				handleClass(clazz, factoryList);
			}
			factoryList.forEach(IRegistryFactory::factoryComplete);
		}

		RebornCore.LOGGER.info("Loaded registrys for " + event.getClass().getName() + " in " + (System.currentTimeMillis() - start) + "ms");
	}

	private void handleClass(Class clazz, List<IRegistryFactory> factories) {
		RebornRegister annotation = (RebornRegister) getAnnoation(clazz.getAnnotations(), RebornRegister.class);
		Validate.isTrue(annotation.value().equals(modid));
		for (IRegistryFactory regFactory : factories) {
			for (Field field : clazz.getDeclaredFields()) {
				if (!regFactory.getTargets().contains(RegistryTarget.FIELD)) {
					continue;
				}
				if (field.isAnnotationPresent(regFactory.getAnnotation())) {
					regFactory.handleField(modid, field);
				}
			}
			for (Method method : clazz.getDeclaredMethods()) {
				if (!regFactory.getTargets().contains(RegistryTarget.MEHTOD)) {
					continue;
				}
				if (method.isAnnotationPresent(regFactory.getAnnotation())) {
					regFactory.handleMethod(modid, method);
				}

			}
			if (regFactory.getTargets().contains(RegistryTarget.CLASS)) {
				if (clazz.isAnnotationPresent(regFactory.getAnnotation())) {
					regFactory.handleClass(modid, clazz);
				}
			}
		}
	}

	private List<IRegistryFactory> getFactorysForSate(LoadStage stage) {
		return factoryList.stream().filter(factory -> factory.getProcessSate() == stage).collect(Collectors.toList());
	}

	public static Annotation getAnnoationFromArray(Annotation[] annotations, IRegistryFactory factory) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == factory.getAnnotation()) {
				return annotation;
			}
		}
		return null;
	}

	public static Annotation getAnnoation(Annotation[] annotations, Class annoation) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == annoation) {
				return annotation;
			}
		}
		return null;
	}

	private void loadFactorys() {
		RebornCore.LOGGER.info("Loaded " + factoryList.size() + " factories for " + modid);
	}

}
