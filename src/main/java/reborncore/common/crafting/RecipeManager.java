package reborncore.common.crafting;

import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RecipeManager {

	private static final Map<ResourceLocation, RecipeType<?>> recipeTypes = new HashMap<>();

	public static <R extends Recipe> RecipeType<R> newRecipeType(Class<R> clazz, ResourceLocation name){
		if(recipeTypes.containsKey(name)){
			throw new RuntimeException("Recipe type with this name already registered");
		}
		RecipeType<R> type = new RecipeType<>(clazz, name);
		recipeTypes.put(name, type);

		RecipeSerializers.register(type);

		return type;
	}

	public static RecipeType<?> getRecipeType(ResourceLocation name){
		if(!recipeTypes.containsKey(name)){
			throw new RuntimeException("Recipe type " + name + " not found");
		}
		return recipeTypes.get(name);
	}

}
