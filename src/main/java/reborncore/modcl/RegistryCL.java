package reborncore.modcl;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Prospector
 */
public abstract class RegistryCL {

	public LinkedHashMap<String, ItemCL> registry = new LinkedHashMap<>();
	public HashMap<ItemStack, String> oreEntries = new HashMap<>();

	public abstract void init(ModCL mod);
}
