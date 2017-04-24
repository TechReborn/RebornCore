package teamreborn.reborncore.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import teamreborn.reborncore.api.registry.FluidFactoryContainer;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Mark on 26/02/2017.
 */
public class RebornBlockRegistry {

	public static void registerBlock(Block block) {
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block), block.getRegistryName());
	}

	public static void registerBlockNoItemBlock(Block block) {
		GameRegistry.register(block);
	}

	public static FluidFactoryContainer registerFluid(FluidFactoryContainer fluidFactoryContainer) {
		FluidRegistry.registerFluid(fluidFactoryContainer.fluid);
		fluidFactoryContainer.block = new FluidBlockBase(fluidFactoryContainer.fluid, fluidFactoryContainer.material);
		GameRegistry.register(fluidFactoryContainer.block);
		FluidRegistry.addBucketForFluid(fluidFactoryContainer.fluid);
		return fluidFactoryContainer;
	}

	public static void registerBlock(Block block, Class<? extends ItemBlock> itemclass) {
		GameRegistry.register(block);
		try {
			ItemBlock itemBlock = itemclass.getConstructor(Block.class).newInstance(block);
			itemBlock.setRegistryName(block.getRegistryName());
			GameRegistry.register(itemBlock);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
