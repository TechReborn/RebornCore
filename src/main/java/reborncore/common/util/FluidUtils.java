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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidUtils {
	public static boolean fluidEquals(Fluid f1, Fluid f2) {
		if (f1.equals(f2)) {
			return true;
		}
		// This is a work around TR's fucked fluid names that we dont want to break
		// worlds in 1.12 to fix. //TODO remove in 1.13
		String s1 = f1.getName();
		String s2 = f2.getName();
		if (s1.startsWith("fluid")) {
			s1 = s1.replaceFirst("fluid", "");
		}
		if (s2.startsWith("fluid")) {
			s2 = s2.replaceFirst("fluid", "");
		}
		return s1.equals(s2);
	}

	public static boolean fillContainers(IFluidHandler source, IInventory inventory, int inputSlot, int outputSlot, Fluid fluidToFill) {
		ItemStack inputStack = inventory.getStackInSlot(inputSlot);
		ItemStack outputStack = inventory.getStackInSlot(outputSlot);

		if (inputStack.isEmpty()) return false;

		if (outputStack.getCount() >= outputStack.getMaxStackSize()) return false;

		// The copy is needed to get the filled container without altering the original ItemStack.
		ItemStack copy = inputStack.copy();
		copy.setCount(1);
		IFluidHandlerItem target = getFluidHandler(copy);

		if (target == null) return false;

		int targetCapacity = target.getTankProperties()[0].getCapacity();

		// Making a simulation to check if the container can be filled from the source.
		FluidStack simulated = FluidUtil.tryFluidTransfer(target, source, targetCapacity, false);
		if (simulated == null || simulated.amount < targetCapacity) return false;

		if (!outputStack.isEmpty() && !ItemUtils.isItemEqual(FluidUtils.getFilledContainer(fluidToFill, inputStack.copy()),
			outputStack, true, true))
			return false;

		// Changes are really applied and the container is filled.
		FluidUtil.tryFluidTransfer(target, source, targetCapacity, true);
		inventory.setInventorySlotContents(inputSlot, ItemUtils.decreaseSize(inputStack));

		// Proceed with inventory changes
		if (outputStack.isEmpty())
			inventory.setInventorySlotContents(outputSlot, target.getContainer());
		else
			ItemUtils.increaseSize(outputStack);

		return true;
	}

	public static boolean drainContainers(IFluidHandler target, IInventory inventory, int inputSlot, int outputSlot) {
		ItemStack inputStack = inventory.getStackInSlot(inputSlot);
		ItemStack outputStack = inventory.getStackInSlot(outputSlot);

		if (inputStack.isEmpty()) return false;

		if (outputStack.getCount() >= outputStack.getMaxStackSize()) return false;

		ItemStack copy = inputStack.copy();
		copy.setCount(1);
		IFluidHandlerItem source = getFluidHandler(copy);

		if (source == null) return false;

		int sourceCapacity = source.getTankProperties()[0].getCapacity();

		// Making a simulation to check if the container can be drained into the target.
		FluidStack simulated = FluidUtil.tryFluidTransfer(target, source, sourceCapacity, false);
		if (simulated == null || simulated.amount < sourceCapacity) return false;

		if (!outputStack.isEmpty() && !ItemUtils.isItemEqual(getEmptyContainer(inputStack.copy()),
			outputStack, true, true))
			return false;

		// Changes are really applied and the container is drained.
		FluidUtil.tryFluidTransfer(target, source, sourceCapacity, true);
		inventory.setInventorySlotContents(inputSlot, ItemUtils.decreaseSize(inputStack));

		// Proceed with inventory changes
		if (outputStack.isEmpty())
			inventory.setInventorySlotContents(outputSlot, source.getContainer());
		else
			ItemUtils.increaseSize(outputStack);

		return true;
	}

	@Nonnull
	public static ItemStack getEmptyContainer(ItemStack filled) {
		if (filled.isEmpty()) return ItemStack.EMPTY;

		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(filled);
		if (fluidHandler != null)
			fluidHandler.drain(fluidHandler.getTankProperties()[0].getCapacity(), true);

		return filled;
	}

	@Nonnull
	public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty) {
		if (fluid == null || empty.isEmpty())
			return ItemStack.EMPTY;
		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(empty);
		fluidHandler.fill(new FluidStack(fluid, fluidHandler.getTankProperties()[0].getCapacity()), true);
		return empty;
	}

	/**
	 * Helper method to get an {@link IFluidHandlerItem} for an ItemStack.
	 *
	 * Note that the itemStack MUST have a stackSize of 1 if you want to fill or drain it.
	 * You can't fill or drain multiple items at once, if you do then liquid is multiplied or destroyed.
	 *
	 * @param stack the itemstack
	 * @return the IFluidHandler if it has one or null otherwise
	 */
	@Nullable
	public static IFluidHandlerItem getFluidHandler(@Nonnull ItemStack stack) {
		return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) ?
		       stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) : null;
	}

	/**
	 * Helper method to get the fluid contained in an ItemStack
	 * Deprecated in favor of {@link #getFluidContained(ItemStack)}
	 *
	 * @param container the container stack
	 * @return the fluid in the container
	 */
	@Deprecated
	@Nullable
	public static FluidStack getFluidStackInContainer(@Nonnull ItemStack container) {
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

	/**
	 * Helper method to get the fluid contained in an ItemStack
	 *
	 * @param container the container stack
	 * @return the fluid in the container
	 */
	@Nullable
	public static FluidStack getFluidContained(@Nonnull ItemStack container) {
		if (!container.isEmpty()) {
			container = ItemHandlerHelper.copyStackWithSize(container, 1);
			IFluidHandlerItem fluidHandler = getFluidHandler(container);
			if (fluidHandler != null)
				return fluidHandler.drain(Integer.MAX_VALUE, false);
		}

		return null;
	}
}
