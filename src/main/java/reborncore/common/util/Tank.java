package reborncore.common.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import reborncore.common.network.packet.CustomDescriptionPacket;
import reborncore.common.packets.PacketHandler;

public class Tank extends FluidTank
{

	private final String name;

	private FluidStack lastBeforeUpdate = null;

	Fluid lastFluid;

	public Tank(String name, int capacity, TileEntity tile)
	{
		super(capacity);
		this.name = name;
		this.tile = tile;
	}

	public boolean isEmpty()
	{
		return getFluid() == null || getFluid().amount <= 0;
	}

	public boolean isFull()
	{
		return getFluid() != null && getFluid().amount >= getCapacity();
	}

	public Fluid getFluidType()
	{
		return getFluid() != null ? getFluid().getFluid() : null;
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound tankData = new NBTTagCompound();
		super.writeToNBT(tankData);
		nbt.setTag(name, tankData);
		return nbt;
	}

	public void setFluidAmount(int amount){
		if(fluid != null){
			fluid.amount = amount;
		}
	}

	@Override
	public final FluidTank readFromNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey(name))
		{
			// allow to read empty tanks
			setFluid(null);

			NBTTagCompound tankData = nbt.getCompoundTag(name);
			super.readFromNBT(tankData);
		}
		return this;
	}

	//TODO optimise
	public void compareAndUpdate()
	{
		if (tile == null || tile.getWorld().isRemote) {
			return;
		}
		if(lastFluid != this.getFluid().getFluid()){
			lastFluid = this.getFluid().getFluid();
			reborncore.common.network.NetworkManager.sendToAllAround(new CustomDescriptionPacket(tile), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 20));
		}
	}

}
