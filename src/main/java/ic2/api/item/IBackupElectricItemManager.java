package ic2.api.item;

import net.minecraft.item.ItemStack;

public interface IBackupElectricItemManager extends IElectricItemManager
{
	boolean handles(ItemStack p0);
}
