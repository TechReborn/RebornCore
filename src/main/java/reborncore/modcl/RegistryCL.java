package reborncore.modcl;

import net.minecraft.item.ItemStack;

import java.util.HashMap;

/**
 * Created by Prospector
 */
public abstract class RegistryCL {

	public HashMap<String, ItemCL> registry = new HashMap<>();
	public HashMap<ItemStack, String> oreEntries = new HashMap<>();

	public abstract void init(ModCL mod);
}
