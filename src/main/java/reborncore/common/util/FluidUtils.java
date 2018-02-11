/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidUtils {

	public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		ItemStack output = inv.getStackInSlot(outputSlot);

		IFluidHandlerItem inputFluidHandler = getFluidHandler(input);

		if (inputFluidHandler != null) {

			/*
			 * Making a simulation to check if the fluid can be drained into the
			 * fluidhandler.
			 */
			if (FluidUtil.tryFluidTransfer(fluidHandler, inputFluidHandler,
				inputFluidHandler.getTankProperties()[0].getCapacity(), false) != null) {

				// Changes are really applied and the fluid is drained.
				FluidStack drained = FluidUtil.tryFluidTransfer(fluidHandler, inputFluidHandler,
					inputFluidHandler.getTankProperties()[0].getCapacity(), true);

				/*
				 * If the drained container doesn't disappear we need to update
				 * the inventory accordingly.
				 */
				if (drained != null && inputFluidHandler.getContainer() != ItemStack.EMPTY)
					if (output.isEmpty()) {
						inv.setInventorySlotContents(outputSlot, inputFluidHandler.getContainer());
						inv.decrStackSize(inputSlot, 1);
					} else {

						/*
						 * When output is not EMPTY, it is needed to check if
						 * the two stacks can be merged together, there was no
						 * simple way to make that check before.
						 */
						if (ItemUtils.isItemEqual(output, inputFluidHandler.getContainer(), true, true)) {
							inv.getStackInSlot(outputSlot).setCount(inv.getStackInSlot(outputSlot).getCount() + 1);
							inv.decrStackSize(inputSlot, 1);
						} else {

							/*
							 * Due to the late check of stacks merge we need to
							 * reverse any changes made to the FluidHandlers
							 * when the merge fail.
							 */
							FluidUtil.tryFluidTransfer(inputFluidHandler, fluidHandler, drained.amount, true);
							return false;
						}
					}
				return true;
			}
		}
		return false;
	}

	public static boolean fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot,
	                                     Fluid fluidToFill) {
		ItemStack input = inv.getStackInSlot(inputSlot);
		ItemStack output = inv.getStackInSlot(outputSlot);

		if (!input.isEmpty()) {
			IFluidHandlerItem inputFluidHandler = getFluidHandler(input);

			/*
			 * The copy is needed to get the filled container without altering
			 * the original ItemStack.
			 */
			ItemStack containerCopy = input.copy();
			containerCopy.setCount(1);

			/*
			 * It's necessary to check before any alterations that the resulting
			 * ItemStack can be placed into the outputSlot.
			 */
			if (inputFluidHandler != null && (output.isEmpty()
				|| (output.getCount() < output.getMaxStackSize() && ItemUtils.isItemEqual(
				FluidUtils.getFilledContainer(fluidToFill, containerCopy), output, true, true)))) {

				/*
				 * Making a simulation to check if the fluid can be transfered
				 * into the fluidhandler.
				 */
				if (FluidUtil.tryFluidTransfer(inputFluidHandler, fluidHandler,
					inputFluidHandler.getTankProperties()[0].getCapacity(), false) != null) {

					// Changes are really applied and the fluid is transfered.
					FluidUtil.tryFluidTransfer(inputFluidHandler, fluidHandler,
						inputFluidHandler.getTankProperties()[0].getCapacity(), true);

					// The inventory is modified and stacks are merged.
					if (output.isEmpty())
						inv.setInventorySlotContents(outputSlot, inputFluidHandler.getContainer());
					else
						inv.getStackInSlot(outputSlot).setCount(inv.getStackInSlot(outputSlot).getCount() + 1);
					inv.decrStackSize(inputSlot, 1);
					return true;
				}
			}
		}
		return false;
	}

	@Nullable
	public static IFluidHandlerItem getFluidHandler(ItemStack container) {
		ItemStack copy = container.copy();
		copy.setCount(1);
		return FluidUtil.getFluidHandler(copy);
	}

	@Nullable
	public static FluidStack getFluidStackInContainer(
		@Nonnull
			ItemStack container) {
		if (!container.isEmpty()) {
			container = container.copy();
			container.setCount(1);
			final IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(container);
			if (fluidHandler != null) {
				return fluidHandler.drain(Fluid.BUCKET_VOLUME, false);
			}
		}
		return null;
	}

	@Nonnull
	public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty) {
		if (fluid == null || empty.isEmpty())
			return ItemStack.EMPTY;
		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(empty);
		fluidHandler.fill(new FluidStack(fluid, fluidHandler.getTankProperties()[0].getCapacity()), true);
		return empty;
	}

}
