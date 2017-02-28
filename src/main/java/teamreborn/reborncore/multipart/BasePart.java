package teamreborn.reborncore.multipart;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import teamreborn.reborncore.api.multipart.IPart;
import teamreborn.reborncore.api.multipart.IPartContainer;

import java.util.List;

public abstract class BasePart implements IPart
{

	@Override
	public BlockStateContainer createBlockState(IPartContainer container)
	{
		return null;
	}

	@Override
	public IBlockState getDefaultState(IPartContainer container)
	{
		return null;
	}

	@Override
	public IBlockState getExtendedState(IPartContainer container)
	{
		return null;
	}

	@Override
	public IBlockState getActualState(IPartContainer container)
	{
		return null;
	}

	@Override
	public List<AxisAlignedBB> getSelectionBoxes(IPartContainer container)
	{
		return getCollisonBoxes(container, null);
	}

	@Override
	public List<AxisAlignedBB> getOcculusionBoxes(IPartContainer container)
	{
		return getCollisonBoxes(container, null);
	}

	@Override
	public ItemStack getPickBlock(IPartContainer container)
	{
		return getDrops(container).get(0);
	}

	@Override
	public void update(IPartContainer container)
	{

	}

	@Override
	public void onNeighborChange(IPartContainer container)
	{

	}

	@Override
	public boolean onBlockActivated(IPartContainer container, EntityPlayer playerIn, EnumHand hand)
	{
		return false;
	}

	@Override
	public void onAdded(IPartContainer container)
	{

	}

	@Override
	public void onRemoved(IPartContainer container)
	{

	}

	@Override
	public ModelResourceLocation getModel(IPartContainer container)
	{
		return null;
	}

	@Override
	public void readFromNBT(IPartContainer container, NBTTagCompound compound)
	{

	}

	@Override
	public NBTTagCompound writeToNBT(IPartContainer container, NBTTagCompound compound)
	{
		return null;
	}
}
