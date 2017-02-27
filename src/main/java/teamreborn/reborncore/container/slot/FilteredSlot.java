package teamreborn.reborncore.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class FilteredSlot extends ListenerSlot
{

	private Predicate<ItemStack> filter;

	public FilteredSlot(final IInventory inventory, final int index, final int xPosition, final int yPosition)
	{
		super(inventory, index, xPosition, yPosition);
	}

	public FilteredSlot setFilter(final Predicate<ItemStack> filter)
	{
		this.filter = filter;
		return this;
	}

	@Override
	public boolean isItemValid(final ItemStack stack)
	{
		return this.filter.test(stack);
	}
}
