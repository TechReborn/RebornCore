package reborncore.api.newRecipe;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface IRecipeFactory<T extends IRecipe> {

	ResourceLocation getName();

	T load(JsonObject jsonObject, ResourceLocation name);

	void addRecipe(IRecipe recipe);

	void removeRecipe(ResourceLocation resourceLocation);

	/**
	 * Removes all recipes, used for reloading the recipes.
	 */
	void clear();

	List<IRecipe> getRecipes();

}
