package ic2.api.item;

import net.minecraft.item.ItemStack;

public interface ISpecialElectricItem extends IElectricItem
{
	IElectricItemManager getManager(ItemStack p0);
}
