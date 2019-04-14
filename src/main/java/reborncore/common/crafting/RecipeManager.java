package reborncore.common.crafting;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.*;

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

	public static <R extends Recipe> List<R> getRecipes(World world, RecipeType<R> type){
		List<R> recipes = new ArrayList<>();
		for(IRecipe recipe : world.getRecipeManager().getRecipes()){
			if(recipe instanceof Recipe && ((Recipe) recipe).getRecipeType().equals(type)){
				if(type.getRecipeClass() != recipe.getClass()){
					throw new RuntimeException("Invalid recipe in " + type.getName());
				}
				//noinspection unchecked
				recipes.add((R) recipe);
			}
		}
		return Collections.unmodifiableList(recipes);
	}

}
