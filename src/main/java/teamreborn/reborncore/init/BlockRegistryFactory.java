package teamreborn.reborncore.init;

import net.minecraft.block.Block;
import teamreborn.reborncore.api.registry.BlockRegistry;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.block.RebornBlockRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@IRegistryFactory.RegistryFactory
public class BlockRegistryFactory  implements IRegistryFactory {
	@Override
	public Class<? extends Annotation> getAnnotation() {
		return BlockRegistry.class;
	}

	@Override
	public void handleField(Field field){
		Class clazz = field.getType();
		if(!Modifier.isStatic(field.getModifiers())){
			throw new RuntimeException("Field must be static when used with RebornBlockRegistry");
		}
		try {
			//TODO constrcutor check
			Block block = (Block) clazz.newInstance();
			RebornBlockRegistry.registerBlock(block);
			field.set(null, block);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
