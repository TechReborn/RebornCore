package reborncore.common.logic;

import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class LogicControllerRegistryFactory implements IRegistryFactory
{

	@Override
	public Class<? extends Annotation> getAnnotation()
	{
		return LogicControllerRegistry.class;
	}

	@Override
	public void handleField(Field field) {
		Class clazz = field.getType();
		if (!Modifier.isStatic(field.getModifiers()))
		{
			throw new RuntimeException("Field must be static when used with RebornBlockRegistry");
		}
		try
		{
			LogicControllerRegistry annotation = (LogicControllerRegistry) RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
			if(annotation == null || annotation.name().isEmpty())
			{
				throw new RuntimeException("No name provided");
			}

			LogicController tile = (LogicController) clazz.newInstance();
			tile.setName(annotation.name());
			LogicBlock block = new LogicBlock(tile);
			LogicUtils.registerLogicController(block, tile);
			field.set(null, tile);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public List<RegistryTarget> getTargets()
	{
		return Collections.singletonList(RegistryTarget.FIELD);
	}
}
