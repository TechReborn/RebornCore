package teamreborn.reborncore.container.slot;

import java.util.function.Consumer;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ListenerSlot extends Slot
{
    private Consumer<ItemStack> onChange;

    public ListenerSlot(final IInventory inventory, final int index, final int x, final int y)
    {
        super(inventory, index, x, y);
    }

    public void setOnChange(final Consumer<ItemStack> onChange)
    {
        this.onChange = onChange;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();

        if (this.onChange != null)
            this.onChange.accept(this.getStack());
    }
}
