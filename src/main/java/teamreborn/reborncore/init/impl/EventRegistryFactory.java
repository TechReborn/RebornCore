package teamreborn.reborncore.init.impl;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.api.registry.RegistryTarget;
import teamreborn.reborncore.api.registry.impl.EventRegistry;
import teamreborn.reborncore.init.RegistrationManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class EventRegistryFactory implements IRegistryFactory
{

	@Override
	public Class<? extends Annotation> getAnnotation()
	{
		return EventRegistry.class;
	}

	@Override
	public void handleClass(Class clazz)
	{
		EventRegistry eventHandler = (EventRegistry) RegistrationManager.getAnnoationFromArray(clazz.getAnnotations(), this);
		boolean registerStatic = false;
		boolean registerDefault = false;
		for(Method method : clazz.getDeclaredMethods()){
			if(Modifier.isStatic(method.getModifiers())){
				if(method.isAnnotationPresent(SubscribeEvent.class)){
					registerStatic = true;
				}
			}
			if(!Modifier.isStatic(method.getModifiers())){
				if(method.isAnnotationPresent(SubscribeEvent.class)){
					registerDefault = true;
				}
			}
		}
		if(registerDefault){
			try {
				MinecraftForge.EVENT_BUS.register(clazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if(registerStatic){
			MinecraftForge.EVENT_BUS.register(clazz);
		}
	}

	@Override
	public List<RegistryTarget> getTargets()
	{
		return Collections.singletonList(RegistryTarget.CLASS);
	}
}
