package ic2.api.recipe;

import net.minecraft.item.ItemStack;

public interface ICraftingRecipeManager
{
	void addRecipe(ItemStack p0, Object... p1);

	void addShapelessRecipe(ItemStack p0, Object... p1);
}
