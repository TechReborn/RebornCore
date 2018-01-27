package reborncore.api.newRecipe;

import net.minecraft.util.ResourceLocation;

/**
 * Machine crafter provides this to the recipe, the recipe then uses this to know of all the possible things that the machine has
 *
 * Some examples of this are:
 * items
 * fluilds
 */
public interface IIngredient<T> {

	public T get();

	public boolean matches(Object obj);

	public Class<? extends T> getHeldClass();

	public ResourceLocation getType();

}
