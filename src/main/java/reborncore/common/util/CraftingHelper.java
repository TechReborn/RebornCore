package reborncore.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.Validate;
import reborncore.RebornCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class CraftingHelper {

	static boolean validateRecipes = false;

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
		ResourceLocation location = getNameForRecipe(outputItemStack);
		ShapedOreRecipe recipe = new ShapedOreRecipe(location, outputItemStack, objectInputs);
		if (validateRecipes) {
			Validate.notNull(outputItemStack);
			Validate.notNull(outputItemStack.getItem());
			if (objectInputs.length == 0) {
				Validate.notNull(null); //Quick way to crash
			}
		}
		CraftingManager.func_193372_a(location, recipe);
	}

	public static void addShapelessOreRecipe(ItemStack outputItemStack, Object... objectInputs) {
		if (validateRecipes) {
			Validate.notNull(outputItemStack);
			Validate.notNull(outputItemStack.getItem());
			if (objectInputs.length == 0) {
				Validate.notNull(null); //Quick way to crash
			}
		}
		ResourceLocation location = getNameForRecipe(outputItemStack);
		CraftingManager.func_193372_a(location, new ShapelessOreRecipe(location, outputItemStack, objectInputs));
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
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (Object input : params) {
			ingredients.add(asIngredient(input));
		}
		ResourceLocation location = getNameForRecipe(output);
		ShapelessRecipes recipe = new ShapelessRecipes(RebornCore.MOD_ID, output, ingredients);
		CraftingManager.func_193372_a(location, recipe);
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
		ResourceLocation location = getNameForRecipe(output);
		IRecipe recipe = createShapedRecipe(output, params);
		CraftingManager.func_193372_a(location, recipe);
		return recipe;
	}

	public static IRecipe createShapedRecipe(ItemStack output, Object... inputs) {
		ArrayList<String> pattern = Lists.newArrayList();
		Map<String, Ingredient> key = Maps.newHashMap();
		Iterator itr = Arrays.asList(inputs).iterator();

		while (itr.hasNext()) {
			Object obj = itr.next();
			if (obj instanceof String) {
				String str = (String) obj;
				if (pattern.size() <= 2) {
					pattern.add(str);
				} else {
					throw new IllegalArgumentException("Recipe has too many crafting rows!");
				}
			} else if (obj instanceof Character) {
				key.put(obj.toString(), asIngredient(itr.next()));
			} else {
				throw new IllegalArgumentException("Unexpected argument of type " + obj.getClass().toString());
			}
		}

		int width = pattern.get(0).length();
		int height = pattern.size();

		key.put(" ", Ingredient.field_193370_a);
		//NonNullList<Ingredient> ingredients = ShapedRecipes.func_192402_a(pattern.toArray(new String[pattern.size()]), key, width, height);
		ShapedRecipes recipe = new ShapedRecipes("biomesoplenty", width, height, NonNullList.create(), output);
		return recipe;
	}

	public static Ingredient asIngredient(Object object) {
		if (object instanceof Item) {
			return Ingredient.func_193367_a((Item) object);
		} else if (object instanceof Block) {
			return Ingredient.func_193369_a(new ItemStack((Block) object));
		} else if (object instanceof ItemStack) {
			return Ingredient.func_193369_a((ItemStack) object);
		}

		throw new IllegalArgumentException("Cannot convert object of type " + object.getClass().toString() + " to an Ingredient!");
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
