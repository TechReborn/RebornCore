package teamreborn.reborncore.init;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.api.registry.RebornRegistry;
import teamreborn.reborncore.api.registry.RegistryTarget;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Mark on 26/02/2017.
 */
public class RegistrationManager {

	static List<IRegistryFactory> factoryList = new ArrayList<>();

	static List<RegistryFactoryInfo> factoryStateList = new ArrayList<>();

	public static void load(FMLPreInitializationEvent event) {
		long start = System.currentTimeMillis();
		final ModContainer activeMod = Loader.instance().activeModContainer();
		ASMDataTable asmDataTable = event.getAsmData();
		loadFactorys(asmDataTable);
		Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(RebornRegistry.class.getName());
		for (ASMDataTable.ASMData data : asmDataSet) {
			try {
				Class clazz = Class.forName(data.getClassName());
				for (Field field : clazz.getDeclaredFields()) {
					for (IRegistryFactory regFactory : factoryList) {
						if (!regFactory.getTargets().contains(RegistryTarget.FIELD)) {
							continue;
						}
						if (field.isAnnotationPresent(regFactory.getAnnotation())) {
							RegistryFactoryInfo info = new RegistryFactoryInfo(regFactory, field, getModID(clazz));
							factoryStateList.add(info);
						}
					}
				}
				for (Method method : clazz.getDeclaredMethods()) {
					for (IRegistryFactory regFactory : factoryList) {
						if (!regFactory.getTargets().contains(RegistryTarget.MEHTOD)) {
							continue;
						}
						if (method.isAnnotationPresent(regFactory.getAnnotation())) {
							RegistryFactoryInfo info = new RegistryFactoryInfo(regFactory, method, getModID(clazz));
							factoryStateList.add(info);
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		for (IRegistryFactory registryFactory : factoryList) {
			if (registryFactory.getTargets().contains(RegistryTarget.CLASS)) {
				Set<ASMDataTable.ASMData> asmDataFactorySet = asmDataTable.getAll(registryFactory.getAnnotation().getName());
				for (ASMDataTable.ASMData data : asmDataFactorySet) {
					try {
						Class handleClazz = Class.forName(data.getClassName());
						Annotation annotation = getAnnoation(handleClazz.getAnnotations(), RebornRegistry.class);
						String modId = Loader.instance().activeModContainer().getModId();
						if (annotation instanceof RebornRegistry) {
							RebornRegistry registryAnnotation = (RebornRegistry) annotation;
							modId = registryAnnotation.value();
						}
						RegistryFactoryInfo info = new RegistryFactoryInfo(registryFactory, handleClazz, modId);
						factoryStateList.add(info);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

				}
			}
		}
		FMLLog.info("Loaded RebornCore Registry system " + (System.currentTimeMillis() - start) + "ms");
		handle(event);
	}

	public static void handle(FMLStateEvent event) {
		long start = System.currentTimeMillis();
		String modIDCache = Loader.instance().activeModContainer().getModId();
		for(RegistryFactoryInfo info : factoryStateList){
			if(info.registryFactory.getProccessEvent().equals(event.getClass())){
				setActiveMod(info.modId);
				for(RegistryTarget target : info.registryFactory.getTargets()){
					switch (target) {
						case CLASS:
							info.registryFactory.handleClass((Class) info.handleObject);
							break;
						case FIELD:
							info.registryFactory.handleField((Field) info.handleObject);
							break;
						case MEHTOD:
							info.registryFactory.handleMethod((Method) info.handleObject);
							break;
					}
				}
			}
		}
		setActiveMod(modIDCache);
		FMLLog.info("Loaded RebornCore " + event.getClass().getSimpleName() + " registry in: " + (System.currentTimeMillis() - start) + "ms");
	}

	public static String getModID(Class clazz) {
		Annotation annotation = getAnnoation(clazz.getAnnotations(), RebornRegistry.class);
		if (annotation instanceof RebornRegistry) {
			RebornRegistry registryAnnotation = (RebornRegistry) annotation;
			String modId = registryAnnotation.value();
			return modId;
		}
		return Loader.instance().activeModContainer().getModId();
	}

	public static Annotation getAnnoationFromArray(Annotation[] annotations, IRegistryFactory factory) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == factory.getAnnotation()) {
				return annotation;
			}
		}
		return null;
	}

	private static Annotation getAnnoation(Annotation[] annotations, Class annoation) {
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
					factoryList.add((IRegistryFactory) object);
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

	private static class RegistryFactoryInfo {

		IRegistryFactory registryFactory;

		Object handleObject;

		String modId;

		public RegistryFactoryInfo(IRegistryFactory registryFactory, Object handleObject, String modId) {
			this.registryFactory = registryFactory;
			this.handleObject = handleObject;
			this.modId = modId;
		}
	}

}
