package reborncore.common.recipes;

import net.minecraft.item.ItemStack;


import java.util.List;

public interface IRecipeInput {

	ItemStack getItemStack();

	List<ItemStack> getAllStacks();
}
