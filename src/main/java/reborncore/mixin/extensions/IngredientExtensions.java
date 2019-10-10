package reborncore.mixin.extensions;

import net.minecraft.item.ItemStack;

import java.util.List;

//because Ingredient.getStackArray() is client-only
public interface IngredientExtensions {
    List<ItemStack> getIngredientStacks();
}
