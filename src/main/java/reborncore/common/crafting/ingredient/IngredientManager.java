package reborncore.common.crafting.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Function;

public class IngredientManager {

	public static final Identifier STACK_RECIPE_TYPE = new Identifier("reborncore", "stack");
	public static final Identifier FLUID_RECIPE_TYPE = new Identifier("reborncore", "fluid");
	public static final Identifier TAG_RECIPE_TYPE = new Identifier("reborncore", "tag");

	private static final HashMap<Identifier, Function<JsonObject, RebornIngredient>> recipeTypes = new HashMap<>();

	public static void setup(){
		recipeTypes.put(STACK_RECIPE_TYPE, StackIngredient::deserialize);
		recipeTypes.put(FLUID_RECIPE_TYPE, FluidIngredient::deserialize);
		recipeTypes.put(TAG_RECIPE_TYPE, TagIngredient::deserialize);
	}

	public static RebornIngredient deserialize(@Nullable JsonElement jsonElement) {
		if(jsonElement == null || !jsonElement.isJsonObject()){
			throw new JsonParseException("ingredient must be a json object");
		}

		JsonObject json = jsonElement.getAsJsonObject();

		Identifier recipeTypeIdent = STACK_RECIPE_TYPE;
		//TODO find a better way to do this.
		if (json.has("fluid")) {
			recipeTypeIdent = FLUID_RECIPE_TYPE;
		} else if (json.has("tag")){
			recipeTypeIdent = TAG_RECIPE_TYPE;
		}

		if(json.has("type")){
			recipeTypeIdent = new Identifier(JsonHelper.getString(json, "type"));
		}

		Function<JsonObject, RebornIngredient> recipeTypeFunction = recipeTypes.get(recipeTypeIdent);
		if(recipeTypeFunction == null){
			throw new JsonParseException("No recipe type found for " + recipeTypeIdent.toString());
		}
		return recipeTypeFunction.apply(json);
	}

}
