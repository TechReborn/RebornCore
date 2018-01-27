package reborncore.common.recipe.factorys;

import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;

import java.util.List;

public class TestRecipe extends BaseRecipe {

	int tickTime;

	public TestRecipe(ResourceLocation resourceLocation, List<IIngredient> inputs, List<IIngredient> outputs) {
		super(resourceLocation, inputs, outputs);
	}

	public int getTickTime() {
		return tickTime;
	}

	public void setTickTime(int tickTime) {
		this.tickTime = tickTime;
	}
}
