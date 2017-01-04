package reborncore.common.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.Validate;

public class CraftingHelper {

	static boolean validateRecipes = false;

	public static void addShapedOreRecipe(ItemStack outputItemStack, Object... objectInputs) {
		ShapedOreRecipe recipe = new ShapedOreRecipe(outputItemStack, objectInputs);
		if (validateRecipes) {
			Validate.notNull(outputItemStack);
			Validate.notNull(outputItemStack.getItem());
			if (objectInputs.length == 0) {
				Validate.notNull(null); //Quick way to crash
			}
			if (recipe.getInput().length == 0) {
				Validate.notNull(null);
			}
		}
		CraftingManager.getInstance().getRecipeList().add(recipe);
	}

	public static void addShapelessOreRecipe(ItemStack outputItemStack, Object... objectInputs) {
		if (validateRecipes) {
			Validate.notNull(outputItemStack);
			Validate.notNull(outputItemStack.getItem());
			if (objectInputs.length == 0) {
				Validate.notNull(null); //Quick way to crash
			}
		}
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(outputItemStack, objectInputs));
	}

	public static void addShapelessRecipe(ItemStack output, Object... params) {
		if (validateRecipes) {
			Validate.notNull(output);
			Validate.notNull(output.getItem());
			if (params.length == 0) {
				Validate.notNull(null); //Quick way to crash
			}
			for (Object obj : params) {
				if (obj instanceof ItemStack) {
					ItemStack stack = (ItemStack) obj;
					Validate.notNull(stack);
					Validate.notNull(stack.getItem());
				}
			}
		}
		GameRegistry.addShapelessRecipe(output, params);
	}

	public static IRecipe addShapedRecipe(ItemStack output, Object... params) {
		if (validateRecipes) {
			Validate.notNull(output);
			Validate.notNull(output.getItem());
			if (params.length == 0) {
				Validate.notNull(null); //Quick way to crash
			}
			for (Object obj : params) {
				if (obj instanceof ItemStack) {
					ItemStack stack = (ItemStack) obj;
					Validate.notNull(stack);
					Validate.notNull(stack.getItem());
				}
			}
		}
		return GameRegistry.addShapedRecipe(output, params);
	}

	public static void addSmelting(Block input, ItemStack output, float xp) {
		if (validateRecipes) {
			Validate.notNull(input);
			Validate.notNull(output);
			Validate.notNull(output.getItem());
		}
		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(Item input, ItemStack output, float xp) {
		if (validateRecipes) {
			Validate.notNull(input);
			Validate.notNull(output);
			Validate.notNull(output.getItem());
		}
		GameRegistry.addSmelting(input, output, xp);
	}

	public static void addSmelting(ItemStack input, ItemStack output, float xp) {
		if (validateRecipes) {
			Validate.notNull(input);
			Validate.notNull(input.getItem());
			Validate.notNull(output);
			Validate.notNull(output.getItem());
		}
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
