package reborncore.api.newRecipe;

import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * A recipe class, this is used to check to see if the input is valid with the
 */
public interface IRecipe {

	/**
	 *
	 * @return The name of the recipe
	 */
	public ResourceLocation getName();

	/**
	 *
	 * @param machine the machine that is trying to craft the recipe
	 * @return if the recipe can be made with the provided ingredients
	 */
	public boolean check(IMachine machine);

	/**
	 * the outputs
	 * @return
	 */
	public List<IIngredient> getOutputs();

}
