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
					return insertIntoInv(sidedInventory, slot, stack);
				}
			}
			return ItemStack.EMPTY;
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
