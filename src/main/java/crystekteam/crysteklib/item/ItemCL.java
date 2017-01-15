package crystekteam.crysteklib.item;

import crystekteam.crysteklib.ModCL;
import net.minecraft.item.Item;

/**
 * Created by Prospector
 */
public abstract class ItemCL extends Item {

	public ItemCL(String name, ModCL mod) {
		setUnlocalizedName(name);
	}
}
