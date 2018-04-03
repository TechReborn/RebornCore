package reborncore.client.multiblock;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

//This is used to render a full height fluid without having an actaull block in the world. Requires an acess transformer to compile/work.
public class RebornFluidRenderer extends BlockFluidRenderer {
	public RebornFluidRenderer() {
		super(BlockColors.init());
	}

	@Override
	public float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial) {
		return 1F;
	}
}
