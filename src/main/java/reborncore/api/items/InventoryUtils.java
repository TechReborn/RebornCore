package reborncore.api.items;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
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
					int transferAmount = Math.min(freeStackSpace, targetStack.getCount());
					if(!simulate){
						targetStack.increment(transferAmount);
					}
					stack.decrement(transferAmount);
				}
			}
		}


		return stack;
	}
}
