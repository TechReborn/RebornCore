package reborncore.common.crafting.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import reborncore.mixin.common.AccessorIngredient;

import java.util.Arrays;
import java.util.List;

public class WrappedIngredient extends RebornIngredient {
    private Ingredient wrapped;

    public WrappedIngredient() {
        super(IngredientManager.WRAPPED_RECIPE_TYPE);
    }

    public WrappedIngredient(Ingredient wrapped) {
        this();
        this.wrapped = wrapped;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return wrapped.test(itemStack);
    }

    @Override
    public Ingredient getPreview() {
        return wrapped;
    }

    @Override
    public List<ItemStack> getPreviewStacks() {
        return Arrays.asList(((AccessorIngredient) (Object) wrapped).getMatchingStacks());
    }

    @Override
    protected JsonObject toJson() {
        if (wrapped.toJson() instanceof JsonObject) {
            return (JsonObject) wrapped.toJson();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("options", wrapped.toJson());
        return jsonObject;
    }

    @Override
    public int getCount() {
        return ((AccessorIngredient) (Object) wrapped).getMatchingStacks().length;
    }

    public static RebornIngredient deserialize(JsonObject jsonObject) {
        Ingredient underlying;
        if (jsonObject.has("options") && jsonObject.get("options") instanceof JsonArray) {
            underlying = Ingredient.fromJson(jsonObject.get("options"));
        }
        else {
            underlying = Ingredient.fromJson(jsonObject);
        }
        return new WrappedIngredient(underlying);
    }
}
