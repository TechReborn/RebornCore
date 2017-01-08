package reborncore.api.recipe;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public class RecipeHandler {

	/**
	 * This is the array list of all of the recipes for all of the machines
	 */
	public static final ArrayList<IBaseRecipeType> recipeList = new ArrayList<>();

	/**
	 * This is a list of all the registered machine names.
	 */
	public static ArrayList<String> machineNames = new ArrayList<>();

	/**
	 * Use this to get all of the recipes form a recipe name
	 *
	 * @param name the name that the recipe was resisted as.
	 * @return A list of all the recipes of a given name.
	 */
	public static List<IBaseRecipeType> getRecipeClassFromName(String name) {
		List<IBaseRecipeType> baseRecipeList = new ArrayList<>();
		for (IBaseRecipeType baseRecipe : recipeList) {
			if (baseRecipe.getRecipeName().equals(name)) {
				baseRecipeList.add(baseRecipe);
			}
		}
		return baseRecipeList;
	}

	public static String getUserFreindlyName(String name) {
		for (IBaseRecipeType baseRecipe : recipeList) {
			if (baseRecipe.getRecipeName().equals(name)) {
				return baseRecipe.getUserFreindlyName();
			}
		}
		return "";
	}

	/**
	 * Add a recipe to the system
	 *
	 * @param recipe The recipe to add to the system.
	 */
	public static void addRecipe(IBaseRecipeType recipe) {
		if (recipe == null) {
			return;
		}
		if (recipeList.contains(recipe)) {
			return;
		}
		// if (!RecipeConfigManager.canLoadRecipe(recipe)) {
		// return;
		// }
		for (Object input : recipe.getInputs()) {
			Validate.notNull(input);
			if(input instanceof ItemStack) {
				Validate.notNull(((ItemStack) input).getItem());
			}
		}
		for (ItemStack output : recipe.getOutputs()) {
			Validate.notNull(output);
			Validate.notNull(output.getItem());
		}
		if (!machineNames.contains(recipe.getRecipeName())) {
			machineNames.add(recipe.getRecipeName());
		}
		recipeList.add(recipe);
	}
}
