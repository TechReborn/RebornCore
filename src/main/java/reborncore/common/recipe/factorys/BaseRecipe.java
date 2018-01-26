package reborncore.common.recipe.factorys;

import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;
import reborncore.api.newRecipe.IInput;
import reborncore.api.newRecipe.IOutput;
import reborncore.api.newRecipe.IRecipe;

import java.util.Collections;
import java.util.List;

public class BaseRecipe implements IRecipe {

	final ResourceLocation resourceLocation;
	final List<IInput> inputs;
	final List<IOutput> outputs;

	public BaseRecipe(ResourceLocation resourceLocation, List<IInput> inputs, List<IOutput> outputs) {
		this.resourceLocation = resourceLocation;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	public ResourceLocation getName() {
		return resourceLocation;
	}

	@Override
	public boolean check(List<IIngredient> ingredients) {
		for(IInput input : inputs){
			boolean found = false;
			for(IIngredient ingredient : ingredients){
				if(input.matches(ingredient)){
					found = true;
				}
			}
			if(!found){
				return false;
			}
		}
		return true;
	}

	@Override
	public List<IOutput> getOutputs() {
		return Collections.unmodifiableList(outputs);
	}
}
