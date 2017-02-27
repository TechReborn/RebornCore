package teamreborn.reborncore.init.impl;

import teamreborn.reborncore.RebornCore;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.api.registry.RegistryTarget;
import teamreborn.reborncore.api.registry.impl.PacketRegistry;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class PacketRegistryFactory implements IRegistryFactory
{

	@Override
	public Class<? extends Annotation> getAnnotation()
	{
		return PacketRegistry.class;
	}

	@Override
	public void handleClass(Class clazz)
	{
		RebornCore.network.register(clazz);
	}

	@Override
	public List<RegistryTarget> getTargets()
	{
		return Collections.singletonList(RegistryTarget.CLASS);
	}
}
