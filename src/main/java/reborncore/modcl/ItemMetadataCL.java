package reborncore.modcl;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prospector
 */
public class ItemMetadataCL extends ItemCL {
	public List<String> types = new ArrayList<>();

	public ItemMetadataCL(ModCL mod, String name, String blockstateLocation) {
		super(mod, name);
		setHasSubtypes(true);
		mod.customBlockStates.put(this, blockstateLocation);
	}

	public ItemMetadataCL(ModCL mod, String name) {
		this(mod, name, "");
	}

	public ItemStack getStack(String name) {
		return getStack(name, 1);
	}

	public ItemStack getStack(String name, int count) {
		for (String type : types) {
			if (type.equalsIgnoreCase(name)) {
				ItemStack stack = new ItemStack(mod.getRegistry().REGISTRY.get(this.name), count, types.indexOf(name));
				stack.setCount(count);
				return stack;
			}
		}
		throw new InvalidParameterException("Stack not found: " + name);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
		for (int meta = 0; meta < types.size(); meta++) {
			list.add(new ItemStack(item, 1, meta));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int meta = itemStack.getItemDamage();
		if (meta < 0 || meta >= types.size()) {
			meta = 0;
		}
		return super.getUnlocalizedName() + "." + types.get(meta);
	}
}
