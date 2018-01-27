package reborncore.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;

import java.util.HashMap;
import java.util.Map;

public class IngredientParser {


	private static Map<ResourceLocation, IIngredient> ingredientList = new HashMap<>();

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


	public static void addIngredient(IIngredient ingredient){
		if(ingredientList.containsKey(ingredient.getType())){
			throw new RuntimeException("Ingdient allready registed!");
		}
		ingredientList.put(ingredient.getType(), ingredient);
	}

}
