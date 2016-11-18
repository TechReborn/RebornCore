package reborncore.client.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;

@Deprecated //use the correct package one
public class SlotFluid extends reborncore.client.gui.slots.SlotFluid
{
	public SlotFluid(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_)
	{
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{

		return FluidContainerRegistry.isContainer(stack)
				|| (stack != ItemStack.EMPTY && stack.getItem() instanceof IFluidContainerItem);

	}
}
