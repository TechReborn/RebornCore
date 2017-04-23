package teamreborn.reborncore.api.registry;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import teamreborn.reborncore.block.FluidBlockBase;

/**
 * Created by Prospector
 */
public class FluidFactoryContainer {
	public Fluid fluid;
	public FluidBlockBase block;
	public Material material;
	public int density;
	public int viscosity;
	public int luminosity;
	public int temperature;
	public boolean gaseous;

	public FluidFactoryContainer(Fluid fluid, FluidBlockBase block, Material material, int density, int viscosity, int luminosity, int temperature, boolean gaseous) {
		this.fluid = fluid;
		this.block = block;
		this.material = material;
		this.density = density;
		this.viscosity = viscosity;
		this.luminosity = luminosity;
		this.temperature = temperature;
		this.gaseous = gaseous;
		fluid.setDensity(density);
		fluid.setViscosity(viscosity);
		fluid.setLuminosity(luminosity);
		fluid.setTemperature(temperature);
		fluid.setGaseous(gaseous);
	}
}
