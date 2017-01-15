package reborncore.modcl;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reborncore.common.IModInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Prospector
 */
public abstract class ModCL implements IModInfo {
	public CreativeTabCL tab = new CreativeTabCL(this);
	public List<Item> modelsToRegister = new ArrayList<>();
	public HashMap<ItemMetadataCL, String> customBlockStates = new HashMap<>();

	public CreativeTabs getTab() {
		return tab;
	}

	public abstract String SERVER_PROXY();

	public abstract String CLIENT_PROXY();

	public ItemStack getTabStack() {
		return ItemStack.EMPTY;
	}

	public String getPrefix() {
		return MOD_ID() + ":";
	}

	private class CreativeTabCL extends CreativeTabs {
		ModCL mod;

		public CreativeTabCL(ModCL mod) {
			super(mod.MOD_ID());
			this.mod = mod;
		}

		@Override
		public ItemStack getTabIconItem() {
			return mod.getTabStack();
		}
	}
}
