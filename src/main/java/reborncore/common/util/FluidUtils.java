package reborncore.common.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidUtils {

	public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		ItemStack output = inv.getStackInSlot(outputSlot);

		if (input != null) {
			FluidStack fluidInContainer = getFluidStackInContainer(input);
			ItemStack emptyItem = input.getItem().getContainerItem(input);
			if (input.getItem() instanceof UniversalBucket) {
				emptyItem = ((UniversalBucket) input.getItem()).getEmpty();
			}
			if (fluidInContainer != null
				&& (emptyItem == null || output == null || (output.getCount() < output.getMaxStackSize()
				&& ItemUtils.isItemEqual(output, emptyItem, true, true)))) {
				int used = fluidHandler.fill(fluidInContainer, false);
				if (used >= fluidInContainer.amount && fluidHandler.fill(fluidInContainer, true) > 0) {
					fluidHandler.fill(fluidInContainer, true);
					if (emptyItem != null)
						if (output == null)
							inv.setInventorySlotContents(outputSlot, emptyItem);
						else
							output.setCount(output.getCount() + 1);
					inv.decrStackSize(inputSlot, 1);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot,
	                                     Fluid fluidToFill) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		ItemStack output = inv.getStackInSlot(outputSlot);
		ItemStack filled = getFilledContainer(fluidToFill, input);
		if (filled != null && (output == ItemStack.EMPTY
			|| (output.getCount() < output.getMaxStackSize() && ItemUtils.isItemEqual(filled, output, true, true)))) {
			FluidStack fluidInContainer = getFluidStackInContainer(filled);
			FluidStack drain = fluidHandler.drain(fluidInContainer, false);
			if (drain != null && drain.amount == fluidInContainer.amount) {
				fluidHandler.drain(fluidInContainer, true);
				if (output == ItemStack.EMPTY)
					inv.setInventorySlotContents(outputSlot, filled);
				else
					output.setCount(output.getCount() + 1);
				inv.decrStackSize(inputSlot, 1);
				return true;
			}
		}
		return false;
	}

	@Deprecated // Use forge one
	@Nullable
	public static FluidStack getFluidStackInContainer(ItemStack stack) {
		return FluidUtil.getFluidContained(stack);
	}

	@Nonnull
	public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty) {
		if (fluid == null || empty == ItemStack.EMPTY)
			return ItemStack.EMPTY;
		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(empty);
		fluidHandler.fill(new FluidStack(fluid, fluidHandler.getTankProperties()[0].getCapacity()), true);
		return empty;
	}

}
