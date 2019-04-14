package reborncore.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public class Recipe implements IRecipe {

	private final RecipeType type;
	private final ResourceLocation name;

	private List<Ingredient> ingredients;
	private List<ItemStack> outputs;
	private int power;
	private int time;

	public Recipe(RecipeType type, ResourceLocation name) {
		this.type = type;
		this.name = name;
	}

	//Only really used for code recipes, try to use json
	public Recipe(RecipeType type, ResourceLocation name, List<Ingredient> ingredients, List<ItemStack> outputs, int power, int time) {
		this.type = type;
		this.name = name;
		this.ingredients = ingredients;
		this.outputs = outputs;
		this.power = power;
		this.time = time;
	}

	public void deserialize(JsonObject jsonObject){
		power = JsonUtils.getInt(jsonObject, "power");
		time = JsonUtils.getInt(jsonObject, "time");
	}

	public void serialize(JsonObject jsonObject){
		jsonObject.addProperty("power", power);
		jsonObject.addProperty("time", time);
	}


	@Override
	public ResourceLocation getId() {
		return name;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return type;
	}

	public RecipeType getRecipeType() {
		return type;
	}

	//Done as our recipes do not support these functions, hopefully nothing blidly calls them

	@Deprecated
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean canFit(int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public ItemStack getRecipeOutput() {
		throw new UnsupportedOperationException();
	}

}
