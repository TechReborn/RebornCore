package reborncore.common.util;

import net.minecraft.item.ItemStack;

public interface ItemDurabilityExtensions {

	default double getDurability(ItemStack stack) {
		return 0;
	}

	default boolean showDurability(ItemStack stack) {
		return false;
	}

	default int getDurabilityColor(ItemStack stack) {
		return 0;
	}

}
