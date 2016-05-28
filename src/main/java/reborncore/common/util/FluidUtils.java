package reborncore.common.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;

public class FluidUtils
{

	public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot)
	{
		ItemStack input = inv.getStackInSlot(inputSlot);
		ItemStack output = inv.getStackInSlot(outputSlot);

		if (input != null)
		{
			FluidStack fluidInContainer = getFluidStackInContainer(input);
			ItemStack emptyItem = input.getItem().getContainerItem(input);
			if(input.getItem() instanceof UniversalBucket){
				emptyItem = ((UniversalBucket) input.getItem()).getEmpty();
			}
			if (fluidInContainer != null
					&& (emptyItem == null || output == null || (output.stackSize < output.getMaxStackSize()
							&& ItemUtils.isItemEqual(output, emptyItem, true, true))))
			{
				int used = fluidHandler.fill(null, fluidInContainer, false);
				if (used >= fluidInContainer.amount && fluidHandler.canFill(EnumFacing.UP, fluidInContainer.getFluid()))
				{
					fluidHandler.fill(null, fluidInContainer, true);
					if (emptyItem != null)
						if (output == null)
							inv.setInventorySlotContents(outputSlot, emptyItem);
						else
							output.stackSize++;
					inv.decrStackSize(inputSlot, 1);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot,
			Fluid fluidToFill)
	{
		ItemStack input = inv.getStackInSlot(inputSlot);
		ItemStack output = inv.getStackInSlot(outputSlot);
		ItemStack filled = getFilledContainer(fluidToFill, input);
		if (filled != null && (output == null
				|| (output.stackSize < output.getMaxStackSize() && ItemUtils.isItemEqual(filled, output, true, true))))
		{
			FluidStack fluidInContainer = getFluidStackInContainer(filled);
			FluidStack drain = fluidHandler.drain(null, fluidInContainer, false);
			if (drain != null && drain.amount == fluidInContainer.amount)
			{
				fluidHandler.drain(null, fluidInContainer, true);
				if (output == null)
					inv.setInventorySlotContents(outputSlot, filled);
				else
					output.stackSize++;
				inv.decrStackSize(inputSlot, 1);
				return true;
			}
		}
		return false;
	}

	public static FluidStack getFluidStackInContainer(ItemStack stack)
	{
		if(stack.getItem() instanceof IFluidContainerItem){
			return  ((IFluidContainerItem) stack.getItem()).getFluid(stack);
		}
		return FluidContainerRegistry.getFluidForFilledItem(stack);
	}

	public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty)
	{
		if (fluid == null || empty == null)
			return null;
		return FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, Integer.MAX_VALUE), empty);
	}

}
