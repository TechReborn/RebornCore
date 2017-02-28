package teamreborn.reborncore.multipart.vanilla;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import teamreborn.reborncore.api.multipart.IPart;
import teamreborn.reborncore.api.multipart.IPartContainer;

public class TileMultipart extends TileEntity implements ITickable, IPartContainer
{

	IPart part;

	@Override
	public void update()
	{
		part.update(this);
	}

	@Override
	public IBlockState getState()
	{
		return world.getBlockState(getPos());
	}

	@Override
	public void onLoad()
	{
		part.onAdded(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		part.readFromNBT(this, compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		return part.writeToNBT(this, super.writeToNBT(compound));
	}
}
