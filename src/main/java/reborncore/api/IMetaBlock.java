package reborncore.api;


import net.minecraft.block.state.IBlockState;

public interface IMetaBlock {

    int amountOfVariants();

    IBlockState getStateFromMetaValue(int meta);
}
