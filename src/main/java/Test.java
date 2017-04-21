import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

/**
 * Created by modmuss50 on 21/04/2017.
 */
public class Test {

	public void test(IBlockState state){
		EnumFacing facing = state.getValue(BlockDirectional.FACING);
	}

}
