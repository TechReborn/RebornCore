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
		//TODO 1.12 read old recipe format
		//addShapedRecipe(outputItemStack, objectInputs);
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

	public NonNullList<Ingredient> parseShapeedRecipes(Object... inputs){
		return NonNullList.create();
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
