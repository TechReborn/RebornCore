package reborncore.common.recipe.factorys;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;
import reborncore.common.recipe.registry.RecipeFacotryRegistry;
import reborncore.common.registration.RebornRegistry;

import java.util.List;

@RebornRegistry
@RecipeFacotryRegistry
public class TestFactory extends BaseRecipeFactory<TestRecipe> {
	public TestFactory() {
		super(new ResourceLocation("reborncore:test"));
	}

	@Override
	public void buildRecipe(JsonObject jsonObject, TestRecipe recipe) {
		recipe.setTickTime(jsonObject.getAsJsonPrimitive("ticktime").getAsInt());
		System.out.println("Set recipe ticktime to " + recipe.getTickTime());
	}

	@Override
	public TestRecipe createRecipe(ResourceLocation resourceLocation, List<IIngredient> inputs, List<IIngredient> outputs) {
		return new TestRecipe(resourceLocation, inputs, outputs);
	}
}
