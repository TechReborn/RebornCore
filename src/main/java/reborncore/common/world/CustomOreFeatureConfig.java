package reborncore.common.world;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.function.Predicate;

public class CustomOreFeatureConfig extends OreFeatureConfig {

	Predicate<BlockState> blockPredicate;

	public CustomOreFeatureConfig(Predicate<BlockState> blockPredicate, BlockState blockState, int size) {
		super(null, blockState, size);
		this.blockPredicate = blockPredicate;
	}


}
