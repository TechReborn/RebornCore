package teamreborn.reborncore.init;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.api.registry.RebornRegistry;

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

	public static void load(FMLPreInitializationEvent event){
		final ModContainer activeMod = Loader.instance().activeModContainer();
		ASMDataTable asmDataTable = event.getAsmData();
		loadFactorys(asmDataTable);
		Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(RebornRegistry.class.getName());
		for(ASMDataTable.ASMData data : asmDataSet){
			if(!data.getAnnotationInfo().isEmpty()){
				String modId = (String) data.getAnnotationInfo().get("modID");
				if(!activeMod.getModId().equals(modId)){
					setActiveMod(modId);
				}
			}
			try {
				Class clazz = Class.forName(data.getClassName());
				for(Field field : clazz.getDeclaredFields()){
					for(IRegistryFactory regFactory : factoryList){
						if(field.isAnnotationPresent(regFactory.getAnnotation())){
							regFactory.handleField(field);
						}
					}
				}
				for(Method method : clazz.getDeclaredMethods()){
					for(IRegistryFactory regFactory : factoryList){
						if(method.isAnnotationPresent(regFactory.getAnnotation())){
							regFactory.handleMethod(method);
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		setActieModContainer(activeMod);
	}

	public static Annotation getAnnoationFromArray(Annotation[] annotations, IRegistryFactory factory){
		for(Annotation annotation : annotations){
			if(annotation.annotationType() == factory.getAnnotation()){
				return annotation;
			}
		}
		return null;
	}

	private static void loadFactorys(ASMDataTable dataTable){
		Set<ASMDataTable.ASMData> asmDataSet = dataTable.getAll(IRegistryFactory.RegistryFactory.class.getName());
		for(ASMDataTable.ASMData data : asmDataSet){
			try {
				Object object = Class.forName(data.getClassName()).newInstance();
				if(object instanceof IRegistryFactory){
					factoryList.add((IRegistryFactory) object);
				}
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	private static void setActiveMod(String modID){
		for(ModContainer modContainer : Loader.instance().getActiveModList()){
			if(modContainer.getModId().equals(modID)){
				setActieModContainer(modContainer);
				break;
			}
		}
	}

	private static void setActieModContainer(ModContainer container){
		Loader.instance().setActiveModContainer(container);
	}


}
