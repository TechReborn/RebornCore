package reborncore.common.recipes;

import net.minecraft.item.ItemStack;

public interface ExtendedRecipeRemainder {

	default ItemStack getRemainderStack(ItemStack stack){
		return ItemStack.EMPTY;
	}

}