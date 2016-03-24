package ic2.api.item;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IItemHudInfo
{
	List<String> getHudInfo(ItemStack p0);
}
