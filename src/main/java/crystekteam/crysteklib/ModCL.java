package crystekteam.crysteklib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import reborncore.common.IModInfo;

/**
 * Created by Prospector
 */
public abstract class ModCL implements IModInfo {
	public CreativeTabCL TAB = new CreativeTabCL(this);

	public CreativeTabs getTab() {
		return TAB;
	}

	public abstract String SERVER_PROXY();

	public abstract String CLIENT_PROXY();

	public ItemStack getTabStack() {
		return ItemStack.EMPTY;
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
