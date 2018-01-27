package reborncore.common.recipe.factorys;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;
import reborncore.api.newRecipe.IRecipe;
import reborncore.api.newRecipe.IRecipeFactory;
import reborncore.common.recipe.IngredientParser;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseRecipeFactory<T extends IRecipe> implements IRecipeFactory {

	ResourceLocation resourceLocation;
	Map<ResourceLocation, IRecipe> recipes = new HashMap<>();

	public BaseRecipeFactory(ResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	@Override
	public ResourceLocation getName() {
		return resourceLocation;
	}

	@Override
	public T load(JsonObject jsonObject, ResourceLocation name) {
		T recipe = createRecipe(name, buildIngredientList(jsonObject.getAsJsonArray("inputs")), buildIngredientList(jsonObject.getAsJsonArray("outputs")));
		buildRecipe(jsonObject, recipe);
		return recipe;
	}

	/**
	 * Use this to load extra info into the IRecipe
	 * @param jsonObject jsonData
	 * @param recipe the recipe to add extra infomation to
	 */
	public abstract void buildRecipe(JsonObject jsonObject, T recipe);

	public abstract T createRecipe(ResourceLocation resourceLocation, List<IIngredient> inputs, List<IIngredient> outputs);

	public List<IIngredient> buildIngredientList(JsonArray jsonElements){
		List<IIngredient> inputList = new ArrayList<>();
		jsonElements.forEach(jsonElement -> {
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
				if(primitive.isString()){
					inputList.add(IngredientParser.parseIngredient(primitive.getAsString()));
				}
			} else if (jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				inputList.add(IngredientParser.parseIngredient(jsonObject));
			} else {
				throw new RuntimeException(getName() + " failed to parse json inputs");
			}
		});
		return inputList;
	}

	@Override
	public void addRecipe(IRecipe recipe) {
		if(recipes.containsKey(recipe.getName())){
			throw new RuntimeException(recipe.getName() + " has allready been registed");
		}
		recipes.put(recipe.getName(), recipe);
	}

	@Override
	public void removeRecipe(ResourceLocation resourceLocation) {
		recipes.remove(resourceLocation);
	}

	@Override
	public void clear() {
		recipes.clear();
	}

	@Override
	public List<IRecipe> getRecipes() {
		//TODO cache this?
		return recipes.entrySet().stream().map(Map.Entry::getValue)
			.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}
}
