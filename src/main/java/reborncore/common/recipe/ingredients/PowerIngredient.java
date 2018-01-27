package reborncore.common.recipe.ingredients;

import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;
import reborncore.api.newRecipe.IMachine;
import reborncore.common.recipe.registry.IngredientRegistry;
import reborncore.common.registration.RebornRegistry;

@RebornRegistry
@IngredientRegistry
public class PowerIngredient implements IIngredient {

	final int power;

	public PowerIngredient(int power) {
		this.power = power;
	}

	@Override
	public boolean canCraft(IMachine machine) {
		return machine.getEnergy().getEnergyStored() > power;
	}

	@Override
	public ResourceLocation getType() {
		return new ResourceLocation("forge:energy");
	}

}
