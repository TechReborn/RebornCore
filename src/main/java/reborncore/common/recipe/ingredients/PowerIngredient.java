package reborncore.common.recipe.ingredients;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;
import reborncore.api.newRecipe.IMachine;
import reborncore.common.recipe.registry.IngredientRegistry;
import reborncore.common.registration.RebornRegistry;

@RebornRegistry
@IngredientRegistry
public class PowerIngredient implements IIngredient {

	int power;

	public PowerIngredient(int power) {
		this.power = power;
	}

	public PowerIngredient() {
	}

	@Override
	public boolean canCraft(IMachine machine) {
		return machine.getEnergy().getEnergyStored() > power;
	}

	@Override
	public ResourceLocation getType() {
		return new ResourceLocation("reborncore:energy");
	}

	public static PowerIngredient fromJson(JsonObject jsonObject){
		if(!jsonObject.has("power")){
			throw new RuntimeException("Power value not specified");
		}
		return new PowerIngredient(jsonObject.getAsJsonPrimitive("power").getAsInt());
	}

}
