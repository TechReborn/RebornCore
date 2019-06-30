package reborncore.mixin.extensions;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface RecipeManagerExtensions {

	<C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAll(RecipeType<T> recipeType_1);

}
