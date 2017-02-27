package teamreborn.reborncore.init;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
public class RegistrationManager
{

	static List<IRegistryFactory> factoryList = new ArrayList<>();

	public static void load(FMLPreInitializationEvent event)
	{
		long start = System.currentTimeMillis();
		final ModContainer activeMod = Loader.instance().activeModContainer();
		ASMDataTable asmDataTable = event.getAsmData();
		loadFactorys(asmDataTable);
		Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(RebornRegistry.class.getName());
		for (ASMDataTable.ASMData data : asmDataSet)
		{
			if (!data.getAnnotationInfo().isEmpty())
			{
				String modId = (String) data.getAnnotationInfo().get("modID");
				if (!activeMod.getModId().equals(modId))
				{
					setActiveMod(modId);
				}
			}
			try
			{
				Class clazz = Class.forName(data.getClassName());
				for (Field field : clazz.getDeclaredFields())
				{
					for (IRegistryFactory regFactory : factoryList)
					{
						if (!regFactory.getTargets().contains(RegistryTarget.FIELD))
						{
							continue;
						}
						if (field.isAnnotationPresent(regFactory.getAnnotation()))
						{
							regFactory.handleField(field);
						}
					}
				}
				for (Method method : clazz.getDeclaredMethods())
				{
					for (IRegistryFactory regFactory : factoryList)
					{
						if (!regFactory.getTargets().contains(RegistryTarget.MEHTOD))
						{
							continue;
						}
						if (method.isAnnotationPresent(regFactory.getAnnotation()))
						{
							regFactory.handleMethod(method);
						}
					}
				}
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		setActiveModContainer(activeMod);
		for (IRegistryFactory registryFactory : factoryList)
		{
			if (registryFactory.getTargets().contains(RegistryTarget.CLASS))
			{
				Set<ASMDataTable.ASMData> asmDataFactorySet = asmDataTable.getAll(registryFactory.getAnnotation().getName());
				for (ASMDataTable.ASMData data : asmDataFactorySet)
				{
					try
					{
						ModContainer activeContainer = Loader.instance().activeModContainer();
						Class factoryClazz = Class.forName(data.getClassName());
						//Check to see if it also has a reborn registry annoation that specifyes a custom mod id
						Annotation annotation = getRegistryAnnoation(factoryClazz.getAnnotations());
						if (annotation instanceof RebornRegistry)
						{
							RebornRegistry registryAnnotation = (RebornRegistry) annotation;
							String modId = registryAnnotation.modID();
							if (!activeContainer.getModId().equals(modId))
							{
								setActiveMod(modId);
							}
						}
						registryFactory.handleClass(factoryClazz);
						setActiveModContainer(activeContainer);
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}

				}
			}
		}
		FMLLog.info("Loaded all registrys in " + (System.currentTimeMillis() - start) + "ms");
	}

	public static Annotation getAnnoationFromArray(Annotation[] annotations, IRegistryFactory factory)
	{
		for (Annotation annotation : annotations)
		{
			if (annotation.annotationType() == factory.getAnnotation())
			{
				return annotation;
			}
		}
		return null;
	}

	private static Annotation getRegistryAnnoation(Annotation[] annotations)
	{
		for (Annotation annotation : annotations)
		{
			if (annotation.annotationType() == RebornRegistry.class)
			{
				return annotation;
			}
		}
		return null;
	}

	private static void loadFactorys(ASMDataTable dataTable)
	{
		Set<ASMDataTable.ASMData> asmDataSet = dataTable.getAll(IRegistryFactory.RegistryFactory.class.getName());
		for (ASMDataTable.ASMData data : asmDataSet)
		{
			try
			{
				Object object = Class.forName(data.getClassName()).newInstance();
				if (object instanceof IRegistryFactory)
				{
					factoryList.add((IRegistryFactory) object);
				}
			}
			catch (ClassNotFoundException | IllegalAccessException | InstantiationException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void setActiveMod(String modID)
	{
		for (ModContainer modContainer : Loader.instance().getActiveModList())
		{
			if (modContainer.getModId().equals(modID))
			{
				setActiveModContainer(modContainer);
				break;
			}
		}
	}

	private static void setActiveModContainer(ModContainer container)
	{
		Loader.instance().setActiveModContainer(container);
	}

}
