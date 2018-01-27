package reborncore.common.recipe.ingredients;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IMachine;
import reborncore.common.recipe.registry.IngredientRegistry;
import reborncore.common.registration.RebornRegistry;

@RebornRegistry
@IngredientRegistry
public class OreIngredient extends BaseIngredient {

	String oreDict;

	public OreIngredient(String oreDict) {
		this.oreDict = oreDict;
	}

	public OreIngredient() {

	}

	@Override
	public boolean canCraft(IMachine machine) {
		return false; //TODO
	}

	@Override
	public ResourceLocation getType() {
		return new ResourceLocation("reborncore:ore");
	}

	public static OreIngredient fromJson(JsonObject jsonObject){
		if(!jsonObject.has("ore")){
			throw new RuntimeException("Ore string not specified");
		}
		return new OreIngredient(jsonObject.getAsJsonPrimitive("ore").getAsString());
	}
}
