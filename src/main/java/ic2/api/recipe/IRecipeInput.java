package ic2.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IRecipeInput
{
	boolean matches(ItemStack p0);

	int getAmount();

	List<ItemStack> getInputs();
}
