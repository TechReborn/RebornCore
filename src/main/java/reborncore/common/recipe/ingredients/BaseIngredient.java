package reborncore.common.recipe.ingredients;

import net.minecraft.util.ResourceLocation;
import reborncore.api.newRecipe.IIngredient;

public abstract class BaseIngredient<T> implements IIngredient {

	final ResourceLocation type;

	public BaseIngredient(ResourceLocation type) {
		this.type = type;
	}

	@Override
	public ResourceLocation getType() {
		return type;
	}
}
