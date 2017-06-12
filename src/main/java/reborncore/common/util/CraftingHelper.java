package reborncore.common.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import reborncore.RebornCore;

public class CraftingHelper {

	public static ResourceLocation getNameForRecipe(ItemStack output) {
		ResourceLocation baseLoc = new ResourceLocation(RebornCore.MOD_ID, output.getItem().getRegistryName().getResourcePath());
		ResourceLocation recipeLoc = baseLoc;
		int index = 0;
		while (CraftingManager.field_193380_a.containsKey(recipeLoc)) {
			index++;
			recipeLoc = new ResourceLocation(RebornCore.MOD_ID, baseLoc.getResourcePath() + "_" + index);
		}
		return recipeLoc;
	}

	public static void addShapedOreRecipe(ItemStack outputItemStack, Object... objectInputs) {
		//		ResourceLocation location = getNameForRecipe(outputItemStack);
		//		ShapedOreRecipe recipe = new ShapedOreRecipe(location, outputItemStack, objectInputs);
		//		CraftingManager.func_193372_a(location, recipe);
	}

	public static void addShapelessOreRecipe(ItemStack outputItemStack, Object... objectInputs) {
		ResourceLocation location = getNameForRecipe(outputItemStack);
		CraftingManager.func_193372_a(location, new ShapelessOreRecipe(location, outputItemStack, objectInputs));
	}

	public static void addShapelessRecipe(ItemStack output, Object... params) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (Object input : params) {
			ingredients.add(toIngredient(input));
		}
		ResourceLocation location = getNameForRecipe(output);
		ShapelessRecipes recipe = new ShapelessRecipes(RebornCore.MOD_ID, output, ingredients);
		CraftingManager.func_193372_a(location, recipe);
	}

	public static IRecipe addShapedRecipe(ItemStack output, Object... params) {
		ResourceLocation location = getNameForRecipe(output);
		IRecipe recipe = null;
		//CraftingManager.func_193372_a(location, recipe);
		return recipe;
	}

	public static Ingredient toIngredient(Object object) {
		if (object instanceof Item) {
			return Ingredient.func_193367_a((Item) object);
		} else if (object instanceof Block) {
			return Ingredient.func_193369_a(new ItemStack((Block) object));
		} else if (object instanceof ItemStack) {
			return Ingredient.func_193369_a((ItemStack) object);
		}

		throw new IllegalArgumentException("Cannot convert object of type " + object.getClass() + " to an Ingredient!");
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
