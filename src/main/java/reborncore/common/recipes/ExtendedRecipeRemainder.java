package reborncore.common.recipes;

import net.minecraft.item.ItemStack;

public interface ExtendedRecipeRemainder {

	default ItemStack getRemainderStack(ItemStack stack){
		return stack.getItem().hasRecipeRemainder() ? new ItemStack(stack.getItem().getRecipeRemainder()) : ItemStack.EMPTY;
	}

}