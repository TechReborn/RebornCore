package reborncore.common.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import reborncore.common.util.Inventory;

/**
 * Created by Mark on 06/06/2016.
 */
public class InventoryStackHandler implements IItemHandler, IItemHandlerModifiable {

    Inventory inventory;

    public InventoryStackHandler(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public int getSlots() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (stack == null || stack.stackSize == 0)
            return null;

        validateSlotIndex(slot);

        ItemStack existing = inventory.contents[slot];

        int limit = getStackLimit(slot, stack);

        if (existing != null)
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.stackSize;
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.stackSize > limit;

        if (!simulate)
        {
            if (existing == null)
            {
                inventory.contents[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
            }
            else
            {
                existing.stackSize += reachedLimit ? limit : stack.stackSize;
            }
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
            return null;

        validateSlotIndex(slot);

        ItemStack existing = inventory.contents[slot];

        if (existing == null)
            return null;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.stackSize <= toExtract)
        {
            if (!simulate)
            {
                inventory.contents[slot] = null;
            }
            return existing;
        }
        else
        {
            if (!simulate)
            {
                inventory.contents[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    protected void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= inventory.contents.length)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + inventory.contents.length + ")");
    }

    protected int getStackLimit(int slot, ItemStack stack)
    {
        return stack.getMaxStackSize();
    }
}
