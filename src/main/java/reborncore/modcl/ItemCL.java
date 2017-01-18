package reborncore.modcl;

import net.minecraft.item.Item;

/**
 * Created by Prospector
 */
public abstract class ItemCL extends Item {

	public ModCL mod;
	public String name;

	public ItemCL(ModCL mod, String name, boolean registerModel) {
		super();
		setInfo(mod, name);
		if (registerModel) {
			mod.itemModelsToRegister.add(this);
		}
	}

	public ItemCL(ModCL mod, String name) {
		this(mod, name, true);
	}

	private void setInfo(ModCL mod, String name) {
		this.mod = mod;
		this.name = name;
		setUnlocalizedName(mod.getPrefix() + name);
		setRegistryName(mod.getModID(), name);
		setCreativeTab(mod.getTab());
	}
}
