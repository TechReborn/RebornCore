package teamreborn.reborncore.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;

/**
 * Created by Prospector
 */
public class FluidBlockBase extends BlockFluidClassic {

	public FluidBlockBase(Fluid fluid, Material material) {
		super(fluid, material);
		setRegistryName(new ResourceLocation(Loader.instance().activeModContainer().getModId(), "fluid" + fluid.getName()));
		setUnlocalizedName(Loader.instance().activeModContainer().getModId() + ":fluid." + fluid.getName());
	}
}