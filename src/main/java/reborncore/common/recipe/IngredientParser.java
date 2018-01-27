package reborncore.common.recipe;

import com.google.gson.JsonObject;
import reborncore.api.newRecipe.IIngredient;

public class IngredientParser {


	public static IIngredient parseIngredient(JsonObject jsonObject){
		if(!jsonObject.has("type")){
			throw new RuntimeException("Recipe input does not have a type assocated to it!");
		}
		String type = jsonObject.get("type").getAsString();
		return null;
	}


	public static IIngredient parseIngredient(String str){
		return null;
	}

}
