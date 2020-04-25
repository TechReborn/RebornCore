package reborncore.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DummyRecipe implements Recipe {

	private final Identifier identifier;
	private final RecipeSerializer<?> recipeSerializer;
	private final RecipeType<?> recipeType;

	public DummyRecipe(Identifier identifier, JsonObject jsonObject) {
		this.identifier = identifier;
		recipeSerializer = Registry.RECIPE_SERIALIZER.get(new Identifier(jsonObject.get("type").getAsString()));
		recipeType = RecipeType.CRAFTING; // ?
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return false;
	}

	@Override
	public ItemStack craft(Inventory inv) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public Identifier getId() {
		return identifier;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return recipeSerializer;
	}

	@Override
	public RecipeType<?> getType() {
		return recipeType;
	}
}
