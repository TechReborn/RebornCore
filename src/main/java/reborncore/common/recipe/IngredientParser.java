package reborncore.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class IngredientParser {

	private static Map<ResourceLocation, IngredientDetails> ingredientList = new HashMap<>();

	public static IIngredient parseIngredient(JsonObject jsonObject){
		if(!jsonObject.has("type")){
			throw new RuntimeException("Recipe input does not have a type assocated to it!");
		}
		String type = jsonObject.get("type").getAsString();
		ResourceLocation resourceLocation = new ResourceLocation(type);
		if(!ingredientList.containsKey(resourceLocation)){
			throw new RuntimeException("Ingredient type of " + resourceLocation + " was not found");
		}

		IngredientDetails ingredientDetails = ingredientList.get(resourceLocation);
		try {
			IIngredient ingredient = (IIngredient) ingredientDetails.fromJsonMethod.invoke(null, jsonObject);
			return ingredient;
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Failed to load ingredient from json", e);
		}
	}

	public static void addIngredient(Class<? extends IIngredient> clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
		IIngredient ingredient = clazz.newInstance();
		if(ingredientList.containsKey(ingredient.getType())){
			throw new RuntimeException("Ingdient allready registed!");
		}
		ingredientList.put(ingredient.getType(), new IngredientDetails(clazz));
	}

	public static class IngredientDetails {
		Class<? extends IIngredient> ingredientClass;
		Method fromJsonMethod;
		ResourceLocation resourceLocation;

		public IngredientDetails(Class<? extends IIngredient> ingredientClass) throws NoSuchMethodException, IllegalAccessException, InstantiationException {
			this.ingredientClass = ingredientClass;
			fromJsonMethod = ingredientClass.getDeclaredMethod("fromJson", JsonObject.class);
			resourceLocation = ingredientClass.newInstance().getType();
		}
	}

}
