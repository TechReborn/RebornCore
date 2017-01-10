package reborncore.client.hud;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Prospector
 */
public abstract class StackInfoElement {
	public boolean isStack;
	public ItemStack stack;
	public Item item;

	public StackInfoElement(ItemStack stack) {
		this.stack = stack;
		isStack = true;
	}

	public StackInfoElement(Item item) {
		this.item = item;
		isStack = false;
	}

	public abstract String getText(ItemStack stack);
}
