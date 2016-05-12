package reborncore.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IWrenchable
{
	boolean wrenchCanSetFacing(EntityPlayer p0, EnumFacing p1);

	EnumFacing getFacing();

	void setFacing(EnumFacing facing);

	boolean wrenchCanRemove(EntityPlayer p0);

	float getWrenchDropRate();

	ItemStack getWrenchDrop(EntityPlayer p0);
}
