package reborncore.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import reborncore.common.util.NonNullListCollector;

import java.util.Collections;
import java.util.List;

public class Recipe implements IRecipe {

	private final RecipeType type;
	private final ResourceLocation name;

	private NonNullList<Ingredient> ingredients;
	private NonNullList<ItemStack> outputs;
	private int power;
	private int time;

	public Recipe(RecipeType type, ResourceLocation name) {
		this.type = type;
		this.name = name;
	}

	//Only really used for code recipes, try to use json
	public Recipe(RecipeType type, ResourceLocation name, NonNullList<Ingredient> ingredients, NonNullList<ItemStack> outputs, int power, int time) {
		this.type = type;
		this.name = name;
		this.ingredients = ingredients;
		this.outputs = outputs;
		this.power = power;
		this.time = time;
	}

	public void deserialize(JsonObject jsonObject){
		//Crash if the recipe has all ready been deserialized
		Validate.isTrue(ingredients == null);

		power = JsonUtils.getInt(jsonObject, "power");
		time = JsonUtils.getInt(jsonObject, "time");

		JsonObject ingredientsJson = JsonUtils.getJsonObject(jsonObject, "ingredients");
		ingredients = ingredientsJson.entrySet().stream().map(entry -> Ingredient.deserialize(entry.getValue())).collect(NonNullListCollector.toList());

		JsonObject resultsJson = JsonUtils.getJsonObject(jsonObject, "results");
		outputs = RecipeUtils.deserializeItems(resultsJson);
	}

	public void serialize(JsonObject jsonObject){
		jsonObject.addProperty("power", power);
		jsonObject.addProperty("time", time);

		//TODO find a way to go backwards on Ingredient's, it seems to write to a packet buffer, so that may help
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

	//TODO unmodifiable
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	public List<ItemStack> getOutputs() {
		return Collections.unmodifiableList(outputs);
	}

	public int getPower() {
		return power;
	}

	public int getTime() {
		return time;
	}

	/**
	 * @param tile the tile that is doing the crafting
	 * @return if true the recipe will craft, if false it will not
	 */
	public boolean canCraft(TileEntity tile){
		return true;
	}

	/**
	 * @param tile the tile that is doing the crafting
	 * @return return true if fluid was taken and should craft
	 */
	public boolean onCraft(TileEntity tile){
		return true; //TODO look into this being a boolean, seems a little odd, not sure what usees it for now
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
