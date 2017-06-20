package reborncore.common.util;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

//TODO remove once https://github.com/MinecraftForge/MinecraftForge/issues/4038 is fixed
public class FixedShapedOreRecipe extends ShapedOreRecipe {

	public FixedShapedOreRecipe(ResourceLocation group,
	                            @Nonnull
		                            ItemStack result, Object... recipe) {
		super(group, result, recipe);
	}

	@Override
	protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++)
		{
			for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++)
			{
				int subX = x - startX;
				int subY = y - startY;
				Ingredient target = Ingredient.EMPTY;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height)
				{
					if (mirror)
					{
						target = input.get(width - subX - 1 + subY * width);
					}
					else
					{
						target = input.get(subX + subY * width);
					}

				}

				if (!target.apply(inv.getStackInRowAndColumn(x, y)))
				{
					return false;
				}
			}
		}

		return true;
	}
}
