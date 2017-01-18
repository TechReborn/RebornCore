package reborncore.common.recipes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Prospector
 */
public abstract class RCRecipeMethods {

	static ItemStack getStack(Item item) {
		return getStack(item, 1);
	}

	static ItemStack getStack(Item item, int count) {
		return getStack(item, count, 0);
	}

	static ItemStack getStack(Item item, boolean wildcard) {
		return getStack(item, 1, true);
	}

	static ItemStack getStack(Item item, int count, boolean wildcard) {
		return getStack(item, count, OreDictionary.WILDCARD_VALUE);
	}

	static ItemStack getStack(Item item, int count, int metadata) {
		return new ItemStack(item, count, metadata);
	}

	static ItemStack getStack(Block block) {
		return getStack(block, 1);
	}

	static ItemStack getStack(Block block, int count) {
		return getStack(block, count, 0);
	}

	static ItemStack getStack(Block block, boolean wildcard) {
		return getStack(block, 1, true);
	}

	static ItemStack getStack(Block block, int count, boolean wildcard) {
		return getStack(block, count, OreDictionary.WILDCARD_VALUE);
	}

	static ItemStack getStack(Block block, int count, int metadata) {
		return getStack(Item.getItemFromBlock(block), count, OreDictionary.WILDCARD_VALUE);
	}
}
