package reborncore.mixin.common;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import reborncore.mixin.extensions.IngredientExtensions;

import java.util.Arrays;
import java.util.List;

@Mixin(Ingredient.class)
public class MixinIngredient implements IngredientExtensions {
    @Shadow private ItemStack[] stackArray;

    @Override
    public List<ItemStack> getIngredientStacks() {
        return Arrays.asList(stackArray);
    }
}
