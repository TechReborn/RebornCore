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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import org.apache.commons.lang3.Validate;
import reborncore.RebornCore;
import reborncore.RebornRegistry;
import reborncore.common.util.serialization.SerializationUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Mark on 26/02/2017.
 */
public class RegistrationManager {

	private static final List<IRegistryFactory> factoryList = new ArrayList<>();

	private String modid;
	private AnnotationModel annotationModel;
	private  List<Class> registryClasses = new ArrayList<>();

	public RegistrationManager(String modid, Class<? extends ModInitializer> modClass) {
		this.modid = modid;
		annotationModel = getAnnotationModel(modClass);
		loadRegistryClasses();
		loadFactorys();
		load(LoadStage.SETUP);
	}

	private AnnotationModel getAnnotationModel(Class<? extends ModInitializer> modClass) {
		String annotationJson = modid + "_annotations.json";
		try(InputStream stream = FabricLauncherBase.getLauncher().getResourceAsStream(annotationJson)){
			return SerializationUtil.GSON.fromJson(new InputStreamReader(stream), AnnotationModel.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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

	private void loadRegistryClasses() {
		annotationModel.findClasses(RebornRegister.class).forEach(classData -> registryClasses.add(getAsClass(classData)));
		RebornCore.LOGGER.info("Loaded " + registryClasses.size() + " registry classes");
	}

	private void loadFactorys() {
		annotationModel.findClasses(IRegistryFactory.RegistryFactory.class).forEach(classData -> {
			Class<?> clazz = getAsClass(classData);
			try {
				IRegistryFactory registryFactory = (IRegistryFactory) clazz.newInstance();
				//TODO check sides and modid
				factoryList.add(registryFactory);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});

		RebornCore.LOGGER.info("Loaded " + factoryList.size() + " factories");
	}

	private Class<?> getAsClass(AnnotationModel.ClassData classData) {
		Validate.notNull(classData);
		Validate.notNull(classData.className);
		try {
			return Class.forName(classData.className.replaceAll("/", "."));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
