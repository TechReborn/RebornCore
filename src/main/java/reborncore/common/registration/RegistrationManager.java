/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
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

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Mark on 26/02/2017.
 */
public class RegistrationManager {

	static HashMap<Class<? extends FMLStateEvent>, IRegistryFactory> factoryList = new HashMap<>();
	static List<Class> registryClasses = new ArrayList<>();

	public static void init(FMLPreInitializationEvent event) {
		long start = System.currentTimeMillis();
		ASMDataTable asmDataTable = event.getAsmData();
		loadFactorys(asmDataTable);

		Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(RebornRegistry.class.getName());
		for (ASMDataTable.ASMData data : asmDataSet) {
			try {
				Class clazz = Class.forName(data.getClassName());
				registryClasses.add(clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		//Sorts all the classes to (try) and ensure they are loaded in the same oder on the client/server.
		//Hopefully this fixes the issue with packets being misaligned
		registryClasses.sort(Comparator.comparing(Class::getCanonicalName));

		for (IRegistryFactory registryFactory : getFactorysForSate(event.getClass())) {
			if (registryFactory.getTargets().contains(RegistryTarget.CLASS)) {
				Set<ASMDataTable.ASMData> asmDataFactorySet = asmDataTable.getAll(registryFactory.getAnnotation().getName());
				for (ASMDataTable.ASMData data : asmDataFactorySet) {
					try {
						ModContainer activeContainer = Loader.instance().activeModContainer();
						Class factoryClazz = Class.forName(data.getClassName());
						//Check to see if it also has a reborn registry annoation that specifyes a custom mod id
						Annotation annotation = getAnnoation(factoryClazz.getAnnotations(), RebornRegistry.class);
						if (annotation instanceof RebornRegistry) {
							RebornRegistry registryAnnotation = (RebornRegistry) annotation;
							String modId = registryAnnotation.modID();
							if (!activeContainer.getModId().equals(modId)) {
								setActiveMod(modId);
							}
						}
						registryFactory.handleClass(factoryClazz);
						setActiveModContainer(activeContainer);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

				}
			}
		}
		FMLLog.info("Pre loaded registries in" + (System.currentTimeMillis() - start) + "ms");
	}

	public static void load(FMLStateEvent event) {
		long start = System.currentTimeMillis();
		final ModContainer activeMod = Loader.instance().activeModContainer();

		List<IRegistryFactory> factoryList = getFactorysForSate(event.getClass());
		if (!factoryList.isEmpty()) {
			for (Class clazz : registryClasses) {
				RebornRegistry annotation = (RebornRegistry) getAnnoation(clazz.getAnnotations(), RebornRegistry.class);
				if (annotation != null) {
					if (!activeMod.getModId().equals(annotation.modID())) {
						setActiveMod(annotation.modID());
					}
				}
				for (Field field : clazz.getDeclaredFields()) {
					for (IRegistryFactory regFactory : factoryList) {
						if (!regFactory.getTargets().contains(RegistryTarget.FIELD)) {
							continue;
						}
						if (field.isAnnotationPresent(regFactory.getAnnotation())) {
							regFactory.handleField(field);
						}
					}
				}
				for (Method method : clazz.getDeclaredMethods()) {
					for (IRegistryFactory regFactory : factoryList) {
						if (!regFactory.getTargets().contains(RegistryTarget.MEHTOD)) {
							continue;
						}
						if (method.isAnnotationPresent(regFactory.getAnnotation())) {
							regFactory.handleMethod(method);
						}
					}
				}
			}
			setActiveModContainer(activeMod);
		}

		FMLLog.info("Loaded registrys for " + event.getClass().getName() + " in " + (System.currentTimeMillis() - start) + "ms");
	}

	private static List<IRegistryFactory> getFactorysForSate(Class<? extends FMLStateEvent> event) {
		List<IRegistryFactory> factorySateList = new ArrayList<>();
		for (Map.Entry<Class<? extends FMLStateEvent>, IRegistryFactory> entry : factoryList.entrySet()) {
			if (entry.getKey() == event) {
				factorySateList.add(entry.getValue());
			}
		}
		return factorySateList;
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

	private static void loadFactorys(ASMDataTable dataTable) {
		Set<ASMDataTable.ASMData> asmDataSet = dataTable.getAll(IRegistryFactory.RegistryFactory.class.getName());
		for (ASMDataTable.ASMData data : asmDataSet) {
			try {
				Class clazz = Class.forName(data.getClassName());
				IRegistryFactory.RegistryFactory registryFactory = (IRegistryFactory.RegistryFactory) getAnnoation(clazz.getAnnotations(), IRegistryFactory.RegistryFactory.class);
				if (!registryFactory.side().canExcetue()) {
					continue;
				}
				if (!Loader.isModLoaded(registryFactory.modID())) {
					continue;
				}
				Object object = clazz.newInstance();
				if (object instanceof IRegistryFactory) {
					IRegistryFactory factory = (IRegistryFactory) object;
					factoryList.put(((IRegistryFactory) object).getProcessSate(), factory);
				}
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	private static void setActiveMod(String modID) {
		for (ModContainer modContainer : Loader.instance().getActiveModList()) {
			if (modContainer.getModId().equals(modID)) {
				setActiveModContainer(modContainer);
				break;
			}
		}
	}

	private static void setActiveModContainer(ModContainer container) {
		Loader.instance().setActiveModContainer(container);
	}

}
