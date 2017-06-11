package reborncore.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;

import java.util.List;

public class RecipeRemover {
	public static void removeShapedRecipes(List<ItemStack> removelist) {
		for (ItemStack stack : removelist)
			removeShapedRecipe(stack);
	}

	public static void removeAnyRecipe(ItemStack resultItem) {
		for(IRecipe tmpRecipe : CraftingManager.field_193380_a){
			ItemStack recipeResult = tmpRecipe.getRecipeOutput();
			if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
				//TODO 1.12
				//recipes.remove(i--);
			}
		}
	}

	public static void removeShapedRecipe(ItemStack resultItem) {
		for(IRecipe tmpRecipe : CraftingManager.field_193380_a){
			if (tmpRecipe instanceof ShapedRecipes) {
				ShapedRecipes recipe = (ShapedRecipes) tmpRecipe;
				ItemStack recipeResult = recipe.getRecipeOutput();

				if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
					//TODO 1.12
					//recipes.remove(i++);
				}
			}
		}
	}
}
