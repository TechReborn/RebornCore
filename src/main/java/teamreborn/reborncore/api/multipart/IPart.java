package teamreborn.reborncore.api.multipart;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * Inital version of a mulitpart
 */
public interface IPart
{

	/**
	 * Must be lower case
	 *
	 * @return
	 */
	public String getIdenteifyer();

	public List<AxisAlignedBB> getCollisonBoxes(IPartContainer container, Entity entity);

	public List<AxisAlignedBB> getSelectionBoxes(IPartContainer container);

	public List<AxisAlignedBB> getOcculusionBoxes(IPartContainer container);

	public void update(IPartContainer container);

	public IBlockState getDefaultState(IPartContainer container);

	public default IBlockState getExtendedState(IPartContainer container)
	{
		return container.getState();
	}

	public default IBlockState getActualState(IPartContainer container)
	{
		return container.getState();
	}

	public List<ItemStack> getDrops(IPartContainer container);

	public ItemStack getPickBlock(IPartContainer container);

	public void onNeighborChange(IPartContainer container);

	public boolean onBlockActivated(IPartContainer container, EntityPlayer playerIn, EnumHand hand);

	public void onAdded(IPartContainer container);

	public void onRemoved(IPartContainer container);

	public ModelResourceLocation getModel(IPartContainer container);

	public void readFromNBT(IPartContainer container, NBTTagCompound compound);

	public NBTTagCompound writeToNBT(IPartContainer container, NBTTagCompound compound);

}
