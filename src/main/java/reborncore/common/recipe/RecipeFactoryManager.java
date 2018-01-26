package reborncore.common.recipe;

import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IRecipeFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeFactoryManager {

	private static Map<ResourceLocation, IRecipeFactory> factorys = new HashMap<>();

	public static void addFactory(IRecipeFactory recipeFactory){
		if(factorys.containsKey(recipeFactory.getName())){
			throw new RuntimeException("Failed to add factory, factory with name: " + recipeFactory.getName() + " has allready been registerd!");
		}
		factorys.put(recipeFactory.getName(), recipeFactory);
	}

	public static IRecipeFactory getRecipeFactory(ResourceLocation resourceLocation){
		return factorys.get(resourceLocation);
	}

	public static List<IRecipeFactory> getFactorys(){
		return factorys.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
	}

}
