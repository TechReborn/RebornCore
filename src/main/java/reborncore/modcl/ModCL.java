package reborncore.modcl;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Prospector
 */
public abstract class ModCL {
	public CreativeTabCL tab = new CreativeTabCL(this);
	public List<Item> modelsToRegister = new ArrayList<>();
	public HashMap<ItemMetadataCL, String> customBlockStates = new HashMap<>();

	public CreativeTabs getTab() {
		return tab;
	}

	public abstract String getServerProxy();

	public abstract String getClientProxy();

	public abstract RegistryCL getItemRegistry();

	public ItemStack getTabStack() {
		return ItemStack.EMPTY;
	}

	public String getPrefix() {
		return getModID() + ":";
	}

	public abstract String getModName();

	public abstract String getModID();

	public abstract String getModVersion();

	public abstract String getModDependencies();

	private class CreativeTabCL extends CreativeTabs {
		ModCL mod;

		public CreativeTabCL(ModCL mod) {
			super(mod.getModID());
			this.mod = mod;
		}

		@Override
		public ItemStack getTabIconItem() {
			return mod.getTabStack();
		}
	}
}
