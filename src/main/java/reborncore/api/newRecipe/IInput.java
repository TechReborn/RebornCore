package reborncore.api.newRecipe;

/**
 * The recipe has a list of inputs that are required to for the recipe to be made
 *
 * Some examples inlcude:
 *
 * items
 * fluids
 *
 * Power isnt an input, as this is handled by the Recipe Factory?
 */
public interface IInput {

	boolean matches(IIngredient ingredient);
}
