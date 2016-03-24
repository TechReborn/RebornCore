package ic2.api.recipe;

import java.util.Map;

import net.minecraft.item.ItemStack;

public interface IScrapboxManager
{
	void addDrop(ItemStack p0, float p1);

	ItemStack getDrop(ItemStack p0, boolean p1);

	Map<ItemStack, Float> getDrops();
}
