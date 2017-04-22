package teamreborn.reborncore.init.impl;

import net.minecraft.block.Block;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.api.registry.RegistryTarget;
import teamreborn.reborncore.api.registry.impl.BlockRegistry;
import teamreborn.reborncore.block.RebornBlockRegistry;
import teamreborn.reborncore.init.RegistrationManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class BlockRegistryFactory implements IRegistryFactory
{

	@Override
	public Class<? extends Annotation> getAnnotation()
	{
		return BlockRegistry.class;
	}

	@Override
	public void handleField(Field field)
	{
		Class clazz = field.getType();
		if (!Modifier.isStatic(field.getModifiers()))
		{
			throw new RuntimeException("Field must be static when used with RebornBlockRegistry");
		}
		try
		{
			Block block = null;
			BlockRegistry annotation = (BlockRegistry) RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
			if (annotation != null && !annotation.param().isEmpty())
			{
				String param = annotation.param();
				block = (Block) clazz.getDeclaredConstructor(String.class).newInstance(param);
			}
			if (block == null)
			{
				block = (Block) clazz.newInstance();
			}
			RebornBlockRegistry.registerBlock(block);
			field.set(null, block);
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			throw new RuntimeException("Failed to load Block", e);
		}
	}

	@Override
	public List<RegistryTarget> getTargets()
	{
		return Collections.singletonList(RegistryTarget.FIELD);
	}
}
