package reborncore.common.recipe.factorys;

import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;
import reborncore.api.newRecipe.IMachine;
import reborncore.api.newRecipe.IRecipe;

import java.util.Collections;
import java.util.List;

public class BaseRecipe implements IRecipe {

	final ResourceLocation resourceLocation;
	final List<IIngredient> inputs;
	final List<IIngredient> outputs;

	public BaseRecipe(ResourceLocation resourceLocation, List<IIngredient> inputs, List<IIngredient> outputs) {
		this.resourceLocation = resourceLocation;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	public ResourceLocation getName() {
		return resourceLocation;
	}

	@Override
	public boolean check(IMachine machine) {
		for(IIngredient input : inputs){
			if(!input.canCraft(machine)){
				return false;
			}
		}
		return true;
	}

	@Override
	public List<IIngredient> getOutputs() {
		return Collections.unmodifiableList(outputs);
	}
}
