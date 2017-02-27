package teamreborn.reborncore.init.impl;

import net.minecraftforge.fml.common.registry.GameRegistry;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.api.registry.RegistryTarget;
import teamreborn.reborncore.api.registry.impl.TileRegistry;
import teamreborn.reborncore.init.RegistrationManager;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class TileRegistryFactory implements IRegistryFactory
{

	@Override
	public Class<? extends Annotation> getAnnotation()
	{
		return TileRegistry.class;
	}

	@Override
	public void handleClass(Class clazz)
	{
		TileRegistry tileRegistry = (TileRegistry) RegistrationManager.getAnnoationFromArray(clazz.getAnnotations(), this);
		GameRegistry.registerTileEntity(clazz, tileRegistry.name());
		System.out.println(tileRegistry.name());
	}

	@Override
	public List<RegistryTarget> getTargets()
	{
		return Collections.singletonList(RegistryTarget.CLASS);
	}
}
