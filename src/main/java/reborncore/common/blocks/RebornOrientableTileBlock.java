/**
 * 
 */
package reborncore.common.blocks;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.api.ToolManager;
import reborncore.api.tile.IWrenchable;
import reborncore.common.BaseTileBlock;
import reborncore.common.items.WrenchHelper;

/**
 * @author drcrazy
 *
 */
public abstract class RebornOrientableTileBlock extends BaseTileBlock implements IWrenchable {
	
	public static PropertyDirection FACING = BlockDirectional.FACING;

	protected RebornOrientableTileBlock() {
		super(Material.IRON);
		setHardness(2f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		BlockWrenchEventHandler.wrenableBlocks.add(this);
	}
	
    protected EnumFacing getFacingForPlacement(World worldIn, BlockPos pos, EntityLivingBase placer) {
        if (placer == null) return EnumFacing.NORTH;

		EnumFacing facing = placer.getHorizontalFacing().getOpposite();
		if (placer.rotationPitch < -50) {
			facing = EnumFacing.DOWN;
		} else if (placer.rotationPitch > 50) {
			facing = EnumFacing.UP;
		}

        return facing;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return null;
	}
	
	// Block
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		EnumFacing newFacing = getFacingForPlacement(worldIn, pos, placer); 
		if (!canSetFacing(worldIn, pos, newFacing, null)) {
			return;
		}
		setFacing(worldIn, pos, newFacing, null);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(EnumHand.MAIN_HAND);
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		// We extended BaseTileBlock. Thus we should always have tile entity. I hope.
		if (tileEntity == null) {
			return false;
		}

		if (!stack.isEmpty() && ToolManager.INSTANCE.canHandleTool(stack)) {
			if (WrenchHelper.handleWrench(stack, worldIn, pos, playerIn, side)) {
				return true;
			}
		}

		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
	}

	// IWrenchable
	@Override
	public EnumFacing getFacing(World world, BlockPos pos) {
		return world.getBlockState(pos).getValue(FACING);
	}

	@Override
	public boolean setFacing(World world, BlockPos pos, EnumFacing newFacing, EntityPlayer player) {
		return world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, newFacing));
	}
}
