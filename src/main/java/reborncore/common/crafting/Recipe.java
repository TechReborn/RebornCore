/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import reborncore.common.util.serialization.SerializationUtil;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Recipe implements IRecipe {

	private final RecipeType<?> type;
	private final ResourceLocation name;

	private NonNullList<RebornIngredient> ingredients;
	private NonNullList<ItemStack> outputs;
	private int power;
	private int time;

	public Recipe(RecipeType<?> type, ResourceLocation name) {
		this.type = type;
		this.name = name;
	}

	//Only really used for code recipes, try to use json
	public Recipe(RecipeType<?> type, ResourceLocation name, NonNullList<RebornIngredient> ingredients, NonNullList<ItemStack> outputs, int power, int time) {
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

		ingredients = SerializationUtil.stream(JsonUtils.getJsonArray(jsonObject, "ingredients"))
			.map(RebornIngredient::deserialize)
			.collect(NonNullListCollector.toList());

		JsonArray resultsJson = JsonUtils.getJsonArray(jsonObject, "results");
		outputs = RecipeUtils.deserializeItems(resultsJson);
	}

	public void serialize(JsonObject jsonObject){
		jsonObject.addProperty("power", power);
		jsonObject.addProperty("time", time);

		List<JsonElement> elements = ingredients.stream().map(RebornIngredient::serialize).collect(Collectors.toList());
		jsonObject.add("ingredients", SerializationUtil.asArray(elements));
	}


	@Override
	public ResourceLocation getId() {
		return name;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return type;
	}

	public RecipeType<?> getRecipeType() {
		return type;
	}

	// use the RebornIngredient version to ensure stack sizes are checked
	@Deprecated
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients.stream().map(RebornIngredient::getBase).collect(NonNullListCollector.toList());
	}

	public NonNullList<RebornIngredient> getRebornIngredients() {
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

	@Override
	public NonNullList<ItemStack> getRemainingItems(IInventory p_179532_1_) {
		throw new UnsupportedOperationException();
	}

	//Done to try and stop the table from loading it
	@Override
	public boolean isDynamic() {
		return true;
	}
}
