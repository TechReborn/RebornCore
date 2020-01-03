/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.api.items;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import reborncore.common.util.ItemUtils;

public class InventoryUtils {

	public static ItemStack insertItemStacked(Inventory inventory, ItemStack input, boolean simulate) {
		ItemStack stack = input.copy();
		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack targetStack = inventory.getInvStack(i);

			//Nice and simple, insert the item into a blank slot
			if(targetStack.isEmpty()){
				if(!simulate){
					inventory.setInvStack(i, stack);
				}
				return ItemStack.EMPTY;
			} else if (ItemUtils.isItemEqual(stack, targetStack, true, false)){
				int freeStackSpace = targetStack.getMaxCount() - targetStack.getCount();
				if(freeStackSpace > 0){
					int transferAmount = Math.min(freeStackSpace, input.getCount());
					if(!simulate){
						targetStack.increment(transferAmount);
					}
					stack.decrement(transferAmount);
				}
			}
		}
		return stack;
	}

	public static ItemStack insertItem(ItemStack input, BlockEntity blockEntity, Direction direction){
		ItemStack stack = input.copy();

		if(blockEntity instanceof SidedInventory){
			SidedInventory sidedInventory = (SidedInventory) blockEntity;
			for(int slot : sidedInventory.getInvAvailableSlots(direction)){
				if(sidedInventory.canInsertInvStack(slot, stack, direction)){
					stack = insertIntoInv(sidedInventory, slot, stack);
					if(stack.isEmpty()){
						break;
					}
				}
			}
			return stack;
		} else if(blockEntity instanceof Inventory){
			Inventory inventory = (Inventory) blockEntity;
			for (int i = 0; i < inventory.getInvSize() & !stack.isEmpty(); i++) {
				stack = insertIntoInv(inventory, i, stack);
			}
		}
		return stack;
	}


	private static ItemStack insertIntoInv(Inventory inventory, int slot, ItemStack input){
		ItemStack targetStack = inventory.getInvStack(slot);
		ItemStack stack = input.copy();

		//Nice and simple, insert the item into a blank slot
		if(targetStack.isEmpty()){
			inventory.setInvStack(slot, stack);
			return ItemStack.EMPTY;
		} else if (ItemUtils.isItemEqual(stack, targetStack, true, false)){
			int freeStackSpace = targetStack.getMaxCount() - targetStack.getCount();
			if(freeStackSpace > 0){
				int transferAmount = Math.min(freeStackSpace, stack.getCount());
				targetStack.increment(transferAmount);
				stack.decrement(transferAmount);
			}
		}

		return stack;
	}
}
