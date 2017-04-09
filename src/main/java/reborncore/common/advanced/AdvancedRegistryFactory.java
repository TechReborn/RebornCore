package reborncore.common.advanced;

import net.minecraft.block.Block;
import reborncore.RebornRegistry;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;
import reborncore.common.registration.impl.BlockRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class AdvancedRegistryFactory implements IRegistryFactory
{

	@Override
	public Class<? extends Annotation> getAnnotation()
	{
		return AdvancedRegistry.class;
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
			AdvancedRegistry annotation = (AdvancedRegistry) RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
			if(annotation == null || annotation.name().isEmpty())
			{
				throw new RuntimeException("No name provided");
			}

			AdvancedTileEntity tile = (AdvancedTileEntity) clazz.newInstance();
			tile.setName(annotation.name());
			AdvancedBlock block = new AdvancedBlock(tile);
			AdvancedUtils.registerAdvanced(block, tile);
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
