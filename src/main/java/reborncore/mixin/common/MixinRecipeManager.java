package reborncore.mixin.common;

import com.google.gson.JsonObject;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import reborncore.common.crafting.ConditionManager;
import reborncore.common.crafting.DummyRecipe;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {

	@Inject(method = "deserialize",at = @At("HEAD"), cancellable = true)
	private static void deserialize(Identifier id, JsonObject json, CallbackInfoReturnable<Recipe<?>> infoReturnable) {
		// TODO dont hard code this as its awful
		if (id.getNamespace().equals("reborncore") || id.getNamespace().equals("techreborn")) {
			if (!ConditionManager.shouldLoadRecipe(json)) {
				infoReturnable.setReturnValue(new DummyRecipe(id, json));
			}
		}
	}
}
