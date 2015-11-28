package reborncore.api;


import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public interface IBlockTextureProvider extends IMetaBlock {

    String getTextureName(IBlockState blockState, EnumFacing facing);
}
