package reborncore.common.crafting.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class DummyIngredient extends RebornIngredient {

	public DummyIngredient() {
		super(new Identifier("reborncore", "dummy"));
	}

	@Override
	public boolean test(ItemStack itemStack) {
		return false;
	}

	@Override
	public Ingredient getPreview() {
		return Ingredient.EMPTY;
	}

	@Override
	public List<ItemStack> getPreviewStacks() {
		return Collections.emptyList();
	}

	@Override
	protected JsonObject toJson() {
		return new JsonObject();
	}

	@Override
	public int getCount() {
		return 0;
	}
}
