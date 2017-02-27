package teamreborn.reborncore.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotOutput extends ListenerSlot
{
    public SlotOutput(final IInventory inventoryIn, final int index, final int xPosition, final int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(final ItemStack stack)
    {
        return false;
    }
}
