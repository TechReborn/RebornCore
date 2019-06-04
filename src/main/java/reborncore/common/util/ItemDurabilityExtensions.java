package reborncore.common.util;

import net.minecraft.item.ItemStack;

public interface ItemDurabilityExtensions {

	default double getDurabilityForDisplay(ItemStack stack) {
		return 0;
	}

	default boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	default int getRGBDurabilityForDisplay(ItemStack stack) {
		return 0;
	}

}
