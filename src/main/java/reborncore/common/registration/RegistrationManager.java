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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.Validate;
import reborncore.Distribution;
import reborncore.RebornCore;
import reborncore.common.util.ScanDataUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
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
		List<ModFileScanData.AnnotationData> annotations = ScanDataUtils.getAnnotations(RebornRegister.class);
		annotations.removeIf(annotationData -> !annotationData.getAnnotationData().get("value").equals(modid));
		annotations.sort(Comparator.comparingInt(RegistrationManager::getPriority));
		for (ModFileScanData.AnnotationData data : annotations) {
			try {
				if (!isModPresent(data)) {
					continue;
				}
				if (!isValidOnSide(data)) {
					continue;
				}
				Class clazz = Class.forName(data.getClassType().getClassName());
				registryClasses.add(clazz);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Failed to load class", e);
			}
		}

		//Sorts all the classes to (try) and ensure they are loaded in the same oder on the client/server.
		//Hopefully this fixes the issue with packets being misaligned
		registryClasses.sort(Comparator.comparing(Class::getCanonicalName));
		RebornCore.LOGGER.info("Pre loaded registries in " + (System.currentTimeMillis() - start) + "ms");
	}

	private static int getPriority(ModFileScanData.AnnotationData annotationData) {
		if (annotationData.getAnnotationData().containsKey("priority")) {
			return -(int) annotationData.getAnnotationData().get("priority");
		}
		return 0;
	}

	private boolean isModPresent(ModFileScanData.AnnotationData annotationData) {
		if (!annotationData.getAnnotationData().containsKey("modOnly")) { //Doesnt have any details about if its only to be loaded along with a specfic mod, so we assume true
			return true;
		}
		String modOnly = (String) annotationData.getAnnotationData().get("modOnly");
		if (modOnly == null || modOnly.isEmpty()) {
			return true;
		}
		for (String modid : modOnly.split(",")) {
			if (modid.startsWith("@")) {
				if (modid.equals("@client")) {
					if (RebornCore.getSide() != Dist.CLIENT) {
						return false;
					}
				}
			} else if (modid.startsWith("!")) {
				if (ModList.get().isLoaded(modid.replaceAll("!", ""))) {
					return false;
				}
			} else {
				if (!ModList.get().isLoaded(modid)) {
					return false;
				}
			}
		}
		return true;
	}

	//This ensures that the class that might be loaded doesnt have any of the common side only markers on it. Its slow but will save us from some common issues
	private boolean isValidOnSide(ModFileScanData.AnnotationData annotationData) {
		String side = RebornCore.getSide().toString().toUpperCase();
		if (annotationData.getAnnotationData().containsKey("side")) {
			ModAnnotation.EnumHolder sideEnum = (ModAnnotation.EnumHolder) annotationData.getAnnotationData().get("side");
			String classSide = sideEnum.getValue();
			if (classSide.equals(Distribution.UNIVERSAL.toString())) {
				//do nothing with univseral classes
			} else if (!side.equals(classSide)) {
				return false;
			}
		}
		//Checks the side only annotations
		List<ModFileScanData.AnnotationData> annotations = ScanDataUtils.getAnnotations(OnlyIn.class);
		for (ModFileScanData.AnnotationData sideData : annotations) {
			if (sideData.getClassType().getClassName().equals(annotationData.getClassType().getClassName())) {
				if (sideData.getAnnotationData().containsKey("value")) {
					ModAnnotation.EnumHolder sideEnum = (ModAnnotation.EnumHolder) sideData.getAnnotationData().get("value");
					String classSide = sideEnum.getValue();
					if (!side.equals(classSide)) {
						return false;
					}
				}
			}
		}
		//Checks for the mod annotation on a class
		annotations = ScanDataUtils.getAnnotations(Mod.class);
		for (ModFileScanData.AnnotationData sideData : annotations) {
			if (sideData.getClassType().getClassName().equals(annotationData.getClassType().getClassName())) {
				if (RebornCore.getSide() == Dist.CLIENT) {
					if (sideData.getAnnotationData().containsKey("serverSideOnly")) {
						boolean value = (boolean) sideData.getAnnotationData().get("serverSideOnly");
						if (value) {
							return false;
						}
					}
				} else {
					if (sideData.getAnnotationData().containsKey("clientSideOnly")) {
						boolean value = (boolean) sideData.getAnnotationData().get("clientSideOnly");
						if (value) {
							return false;
						}
					}
				}

			}
		}
		return true;
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
		List<ModFileScanData.AnnotationData> annotations = ScanDataUtils.getAnnotations(IRegistryFactory.RegistryFactory.class);
		for (ModFileScanData.AnnotationData data : annotations) {
			try {
				Class clazz = Class.forName(data.getClassType().getClassName());
				IRegistryFactory.RegistryFactory registryFactory = (IRegistryFactory.RegistryFactory) getAnnoation(clazz.getAnnotations(), IRegistryFactory.RegistryFactory.class);
				if (!registryFactory.side().canExcetue()) {
					continue;
				}
				if (!ModList.get().isLoaded(registryFactory.modID())) {
					continue;
				}
				Object object = clazz.newInstance();
				if (object instanceof IRegistryFactory) {
					IRegistryFactory factory = (IRegistryFactory) object;
					factory.onInit(modid);
					factoryList.add(factory);
				}
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
				throw new RuntimeException(e);
			}
		}
		RebornCore.LOGGER.info("Loaded " + factoryList.size() + " factories for " + modid);
	}

}
