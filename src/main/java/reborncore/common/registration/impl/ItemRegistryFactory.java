package reborncore.common.registration.impl;

import net.minecraft.item.Item;
import reborncore.RebornRegistry;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class ItemRegistryFactory implements IRegistryFactory {

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return ItemRegistry.class;
	}

	@Override
	public void handleField(Field field) {
		Class clazz = field.getType();
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new RuntimeException("Field must be static when used with RebornBlockRegistry");
		}
		try {
			Item item = null;
			ItemRegistry annotation = (ItemRegistry) RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
			if (annotation != null && !annotation.param().isEmpty()) {
				String param = annotation.param();
				item = (Item) clazz.getDeclaredConstructor(String.class).newInstance(param);
			}
			if (item == null) {
				item = (Item) clazz.newInstance();
			}
			RebornRegistry.registerItem(item);
			field.set(null, item);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<RegistryTarget> getTargets() {
		return Collections.singletonList(RegistryTarget.FIELD);
	}
}
