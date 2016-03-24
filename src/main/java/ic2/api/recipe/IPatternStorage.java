package ic2.api.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IPatternStorage
{
	boolean addPattern(ItemStack p0);

	List<ItemStack> getPatterns();
}
