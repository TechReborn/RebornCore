package reborncore.common.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import reborncore.RebornCore;

import java.util.HashMap;
import java.util.Map;

public class RebornCraftingHelper {

	public static ResourceLocation getNameForRecipe(ItemStack output) {
		ResourceLocation baseLoc = new ResourceLocation(RebornCore.MOD_ID, output.getItem().getRegistryName().getResourcePath());
		ResourceLocation recipeLoc = baseLoc;
		int index = 0;
		while (CraftingManager.REGISTRY.containsKey(recipeLoc)) {
			index++;
			recipeLoc = new ResourceLocation(RebornCore.MOD_ID, baseLoc.getResourcePath() + "_" + index);
		}
		return recipeLoc;
	}

	public static void addShapedOreRecipe(ItemStack outputItemStack, Object... objectInputs) {
		NonNullList<Ingredient> ingredients = parseShapedRecipe(objectInputs);
		ResourceLocation location = getNameForRecipe(outputItemStack);
		ShapedRecipes recipe = new ShapedRecipes(location.toString(), 3, 3, ingredients, outputItemStack);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}

	public static IRecipe addShapedRecipe(ItemStack output, Object... params) {
		ResourceLocation location = getNameForRecipe(output);
		ShapedRecipes recipe = new ShapedRecipes(location.toString(), 3, 3, buildInput(params), output);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
		return recipe;
	}

	public static void addShapelessOreRecipe(ItemStack output, Object... input) {
		ResourceLocation location = getNameForRecipe(output);
		ShapelessOreRecipe recipe = new ShapelessOreRecipe(location, output, input);
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}

	public static void addShapelessRecipe(ItemStack output, Object... input) {
		ResourceLocation location = getNameForRecipe(output);
		ShapelessRecipes recipe = new ShapelessRecipes(location.toString(), output, buildInput(input));
		recipe.setRegistryName(location);
		ForgeRegistries.RECIPES.register(recipe);
	}

	private static NonNullList<Ingredient> buildInput (Object[] input) {
		NonNullList<Ingredient> list = NonNullList.create();
		for(Object obj : input){
			list.add(CraftingHelper.getIngredient(obj));
		}
		return list;
	}

	public static NonNullList<Ingredient> parseShapedRecipe(Object... inputs){
		int size = 0;
		String recipePattern = "";
		Map<Character, Ingredient> ingredientMap  = new HashMap<>();
		boolean hasFoundChar = false;
		for (int i = 0; i < inputs.length; i++) {
			Object object = inputs[i];
			System.out.println(object.getClass());
			if(object instanceof String && !hasFoundChar){
				String str = (String) object;
				if(i == 0){
					size = str.length();
					if(size > 3){
						throw new Error("This shoudnt happen right?");
					}
				}
				if(i > size){
					continue;
				}
				recipePattern += str;
			} else if(object instanceof Character){
				hasFoundChar = true;
				Character character = (Character) object;
				if(ingredientMap.containsKey(character)){
					throw new Error("This shoudnt happen right?");
				}
				System.out.println("Adding:" + character + ":" + CraftingHelper.getIngredient(inputs[i + 1]));
				ingredientMap.put(character, CraftingHelper.getIngredient(inputs[i + 1]));
			}
		}
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for(Character character : recipePattern.toCharArray()){
			if(ingredientMap.containsKey(character)){
				Ingredient ingredient = ingredientMap.get(character);
				if(ingredient == null){
					ingredient = Ingredient.EMPTY;
				}
				ingredients.add(ingredient);
				System.out.println(character + ":" + ingredientMap.get(character));
			} else {
				ingredients.add(Ingredient.EMPTY);
			}
		}
		return ingredients;
	}


	public static void addSmelting(Block input, ItemStack output, float xp) {
		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(Item input, ItemStack output, float xp) {
		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(ItemStack input, ItemStack output, float xp) {
		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(ItemStack input, ItemStack output) {
		addSmelting(input, output, 1F);
	}

	public static void addSmelting(Item input, ItemStack output) {
		addSmelting(input, output, 1F);
	}

	public static void addSmelting(Block input, ItemStack output) {
		addSmelting(input, output, 1F);
	}
}
