package teamreborn.reborncore.init.impl;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import teamreborn.reborncore.api.registry.FluidFactoryContainer;
import teamreborn.reborncore.api.registry.IRegistryFactory;
import teamreborn.reborncore.api.registry.RegistryTarget;
import teamreborn.reborncore.api.registry.impl.FluidRegistry;
import teamreborn.reborncore.block.FluidBlockBase;
import teamreborn.reborncore.block.RebornBlockRegistry;
import teamreborn.reborncore.init.RegistrationManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

@IRegistryFactory.RegistryFactory
public class FluidRegistryFactory implements IRegistryFactory {

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return FluidRegistry.class;
	}

	@Override
	public void handleField(Field field) {
		Class clazz = field.getType();
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new RuntimeException("Field must be static when used with RebornBlockRegistry");
		}
		try {
			FluidFactoryContainer fluidFactoryContainer = null;
			FluidRegistry annotation = (FluidRegistry) RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
			if (annotation != null && !annotation.name().isEmpty() && !annotation.path().isEmpty() && !annotation.material().isEmpty()) {
				Material material = Material.WATER;
				if (annotation.material().equals("lava"))
					material = Material.LAVA;
				fluidFactoryContainer = (FluidFactoryContainer) clazz.getDeclaredConstructor(Fluid.class, FluidBlockBase.class, Material.class, int.class, int.class, int.class, int.class, boolean.class).newInstance(new Fluid(annotation.name(), new ResourceLocation(Loader.instance().activeModContainer().getModId(), annotation.path() + annotation.name() + "_still"), new ResourceLocation(Loader.instance().activeModContainer().getModId(), annotation.path() + annotation.name() + "_flowing")), null, material, annotation.density(), annotation.viscosity(), annotation.luminosity(), annotation.temperature(), annotation.gaseous());
			}

			if (fluidFactoryContainer == null) {
				fluidFactoryContainer = (FluidFactoryContainer) clazz.newInstance();
			}
			FluidFactoryContainer output = RebornBlockRegistry.registerFluid(fluidFactoryContainer);
			field.set(null, output);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException("Failed to load Fluid", e);
		}
	}

	@Override
	public List<RegistryTarget> getTargets() {
		return Collections.singletonList(RegistryTarget.FIELD);
	}
}
