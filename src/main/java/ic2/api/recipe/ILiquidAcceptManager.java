package ic2.api.recipe;

import java.util.Set;

import net.minecraftforge.fluids.Fluid;

public interface ILiquidAcceptManager
{
	boolean acceptsFluid(Fluid p0);

	Set<Fluid> getAcceptedFluids();
}
