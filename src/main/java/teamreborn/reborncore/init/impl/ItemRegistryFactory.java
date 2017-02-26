package teamreborn.reborncore.init.impl;

import net.minecraft.item.Item;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.api.registry.impl.ItemRegistry;
import teamreborn.reborncore.init.RegistrationManager;
import teamreborn.reborncore.item.RebornItemRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

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
			RebornItemRegistry.registerItem(item);
			field.set(null, item);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
