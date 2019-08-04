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
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import reborncore.common.crafting.ingredient.IngredientManager;
import reborncore.common.crafting.ingredient.RebornIngredient;
import reborncore.common.util.NonNullListCollector;
import reborncore.common.util.serialization.SerializationUtil;

import java.util.Collections;
import java.util.List;

public class RebornRecipe implements Recipe {

	private final RebornRecipeType<?> type;
	private final Identifier name;

	private DefaultedList<RebornIngredient> ingredients;
	private DefaultedList<ItemStack> outputs;
	private int power;
	private int time;

	public RebornRecipe(RebornRecipeType<?> type, Identifier name) {
		this.type = type;
		this.name = name;
	}

	public void deserialize(JsonObject jsonObject){
		//Crash if the recipe has all ready been deserialized
		Validate.isTrue(ingredients == null);

		power = JsonHelper.getInt(jsonObject, "power");
		time = JsonHelper.getInt(jsonObject, "time");

		ingredients = SerializationUtil.stream(JsonHelper.getArray(jsonObject, "ingredients"))
			.map(IngredientManager::deserialize)
			.collect(NonNullListCollector.toList());

		JsonArray resultsJson = JsonHelper.getArray(jsonObject, "results");
		outputs = RecipeUtils.deserializeItems(resultsJson);
	}

	public void serialize(JsonObject jsonObject) {
		jsonObject.addProperty("power", power);
		jsonObject.addProperty("time", time);

		JsonArray ingredientsArray = new JsonArray();
		getRebornIngredients().stream().map(RebornIngredient::witeToJson).forEach(ingredientsArray::add);
		jsonObject.add("ingredients", ingredientsArray);

		JsonArray resultsArray = new JsonArray();
		for(ItemStack stack : outputs){
			JsonObject stackObject = new JsonObject();
			stackObject.addProperty("item", Registry.ITEM.getId(stack.getItem()).toString());
			if(stack.getCount() > 1){
				stackObject.addProperty("count", stack.getCount());
			}
			if(stack.hasTag()){
				jsonObject.add("tag", Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, stack.getTag()));
			}
		}
		jsonObject.add("results", resultsArray);
	}

	@Override
	public Identifier getId() {
		return name;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return type;
	}

	@Override
	public net.minecraft.recipe.RecipeType<?> getType() {
		return type;
	}

	public RebornRecipeType getRebornRecipeType(){
		return type;
	}

	// use the RebornIngredient version to ensure stack sizes are checked
	@Deprecated
	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		return ingredients.stream().map(RebornIngredient::getPreview).collect(NonNullListCollector.toList());
	}

	public DefaultedList<RebornIngredient> getRebornIngredients() {
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
	 * @param blockEntity the blockEntity that is doing the crafting
	 * @return if true the recipe will craft, if false it will not
	 */
	public boolean canCraft(BlockEntity blockEntity){
		return true;
	}

	/**
	 * @param blockEntity the blockEntity that is doing the crafting
	 * @return return true if fluid was taken and should craft
	 */
	public boolean onCraft(BlockEntity blockEntity){
		return true; //TODO look into this being a boolean, seems a little odd, not sure what usees it for now
	}

	//Done as our recipes do not support these functions, hopefully nothing blidly calls them

	@Deprecated
	@Override
	public boolean matches(Inventory inv, World worldIn) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public ItemStack craft(Inventory inv) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean fits(int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public ItemStack getOutput() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DefaultedList<ItemStack> getRemainingStacks(Inventory p_179532_1_) {
		throw new UnsupportedOperationException();
	}

	//Done to try and stop the table from loading it
	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}
}
