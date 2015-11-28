package reborncore.api;


import net.minecraft.block.state.IBlockState;

public interface IMetaBlock {

    int amoutOfVariants();

    IBlockState getStateFromMeta(int meta);
}
