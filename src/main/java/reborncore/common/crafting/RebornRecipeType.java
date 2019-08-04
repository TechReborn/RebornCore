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

import com.google.gson.JsonObject;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;
import reborncore.common.util.ExceptionUtils;
import reborncore.common.util.serialization.SerializationUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Supplier;

public class RebornRecipeType<R extends RebornRecipe> implements RecipeType, RecipeSerializer {

	private final Class<R> clazz;

	private final Identifier typeId;

	public RebornRecipeType(Class<R> clazz, Identifier typeId) {
		this.clazz = clazz;
		this.typeId = typeId;
	}

	@Override
	public R read(Identifier recipeId, JsonObject json) {
		Identifier type = new Identifier(JsonHelper.getString(json, "type"));
		if (!type.equals(typeId)) {
			throw new RuntimeException("RebornRecipe type not supported!");
		}

		R recipe = newRecipe(recipeId);
		ExceptionUtils.tryAndThrow(() -> recipe.deserialize(json),"Failed to read " + recipeId);
		return recipe;

	}

	public JsonObject toJson(R recipe) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", typeId.toString());

		recipe.serialize(jsonObject);

		return jsonObject;
	}

	public R fromJson(Identifier recipeType, JsonObject json) {
		return read(recipeType, json);
	}

	R newRecipe(Identifier recipeId) {
		try {
			return clazz.getConstructor(RebornRecipeType.class, Identifier.class).newInstance(this, recipeId);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException("Failed to create new recipe class for " + recipeId + " using " + clazz.getName());
		}
	}

	@Override
	public R read(Identifier recipeId, PacketByteBuf buffer) {
		String input = buffer.readString(buffer.readInt());
		return read(recipeId, SerializationUtil.GSON_FLAT.fromJson(input, JsonObject.class));
	}

	@Override
	public void write(PacketByteBuf buffer, Recipe recipe) {
		JsonObject jsonObject = toJson((R) recipe);
		String output = SerializationUtil.GSON_FLAT.toJson(jsonObject);
		buffer.writeInt(output.length());
		buffer.writeString(output);
	}

	public Identifier getName() {
		return typeId;
	}

	public List<RebornRecipe> getRecipes(World world) {
		return RecipeUtils.getRecipes(world, this);
	}

	public Class<R> getRecipeClass() {
		return clazz;
	}

}
