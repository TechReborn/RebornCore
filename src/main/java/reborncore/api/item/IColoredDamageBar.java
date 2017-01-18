package reborncore.api.item;

import net.minecraft.item.ItemStack;

/**
 * Created by modmuss50 on 18/01/2017.
 */
public interface IColoredDamageBar {

	public boolean showRGBDurabilityBar(ItemStack stack);

	default public int getRGBDurabilityForBar(ItemStack stack){
		return 0xFFE14E1C;
	};
}
