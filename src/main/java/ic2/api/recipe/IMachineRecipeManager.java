package ic2.api.recipe;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IMachineRecipeManager
{
	void addRecipe(IRecipeInput p0, NBTTagCompound p1, ItemStack... p2);

	RecipeOutput getOutputFor(ItemStack p0, boolean p1);

	Map<IRecipeInput, RecipeOutput> getRecipes();
}
