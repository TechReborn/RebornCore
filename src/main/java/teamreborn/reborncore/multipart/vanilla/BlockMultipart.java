package teamreborn.reborncore.multipart.vanilla;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import teamreborn.reborncore.api.multipart.IPart;
import teamreborn.reborncore.api.multipart.IPartContainer;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by modmuss50 on 28/02/2017.
 */
public class BlockMultipart extends BlockContainer
{

	IPart part;

	public BlockMultipart()
	{
		super(Material.IRON);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileMultipart();
	}

	public IPartContainer getContainer(IBlockAccess world, BlockPos pos)
	{
		return (IPartContainer) world.getTileEntity(pos);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
	{
		collidingBoxes.addAll(part.getCollisonBoxes(getContainer(worldIn, pos), entityIn));
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		return part.getDrops(getContainer(world, pos));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return part.getPickBlock(getContainer(world, pos));
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_189540_5_)
	{
		part.onNeighborChange(getContainer(worldIn, pos));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY)
	{
		return part.onBlockActivated(getContainer(worldIn, pos), playerIn, hand);
	}
}
