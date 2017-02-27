package teamreborn.reborncore.container;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.Range;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import teamreborn.reborncore.container.slot.FilteredSlot;
import teamreborn.reborncore.container.slot.ListenerSlot;
import teamreborn.reborncore.container.slot.SlotFuel;
import teamreborn.reborncore.container.slot.SlotOutput;
import teamreborn.reborncore.container.sync.DefaultSyncables;

public class ContainerTileInventoryBuilder
{

    private final IInventory       tile;
    private final ContainerBuilder parent;
    private final int              rangeStart;

    ContainerTileInventoryBuilder(final ContainerBuilder parent, final IInventory tile)
    {
        this.tile = tile;
        this.parent = parent;
        this.rangeStart = parent.slots.size();
    }

    /**
     * Add a basic slot to the container. The specified index is the slot index from the IInventory currently used.
     * @param index
     * @param x
     * @param y
     * @return
     */
    public ContainerTileInventoryBuilder slot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new ListenerSlot(this.tile, index, x, y));
        return this;
    }

    /**
     * Add an output slot to the container. The output slot cannot be filled with items by hand nor automation.
     * The specified index is the slot index from the IInventory currently used.
     * @param index
     * @param x
     * @param y
     * @return
     */
    public ContainerTileInventoryBuilder outputSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new SlotOutput(this.tile, index, x, y));
        return this;
    }

    /**
     * Add a filtered slot to the container. A predicate used for the filtering need to be specified.
     * The specified index is the slot index from the IInventory currently used.
     * @param index
     * @param x
     * @param y
     * @param filter
     * @return
     */
    public ContainerTileInventoryBuilder filterSlot(final int index, final int x, final int y,
            final Predicate<ItemStack> filter)
    {
        this.parent.slots.add(new FilteredSlot(this.tile, index, x, y).setFilter(filter));
        return this;
    }

    /**
     * Add a fluid container-only slot to the container.
     * A FilteredSlot will be created with a predefined predicate that check for the presence of the forge 
     * CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.
     * The specified index is the slot index from the IInventory currently used.
     * @param index
     * @param x
     * @param y
     * @return
     */
    @SuppressWarnings("null")
    public ContainerTileInventoryBuilder fluidSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new FilteredSlot(this.tile, index, x, y).setFilter(
                stack -> stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP)));
        return this;
    }

    /**
     * Add a fuel-only slot to the container. A FilteredSlot will be created with a predefined predicate that check for furnace fuel compatibility.
     * The specified index is the slot index from the IInventory currently used.
     * @param index
     * @param x
     * @param y
     * @return
     */
    public ContainerTileInventoryBuilder fuelSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new SlotFuel(this.tile, index, x, y));
        return this;
    }

    /**
     * Add a syncable Boolean value to the container. Every time the Boolean supplied change it will be sent to the client and set by the provided Consumer.
     * @param supplier
     * @param setter
     * @return
     */
    public ContainerTileInventoryBuilder syncBooleanValue(final Supplier<Boolean> supplier,
            final Consumer<Boolean> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableBoolean(supplier, setter));
        return this;
    }

    /**
     * Add a syncable Integer value to the container. Every time the Integer supplied change it will be sent to the client and set by the provided Consumer.
     * @param supplier
     * @param setter
     * @return
     */
    public ContainerTileInventoryBuilder syncIntegerValue(final Supplier<Integer> supplier,
            final Consumer<Integer> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableInteger(supplier, setter));
        return this;
    }

    /**
     * Add a syncable String value to the container. Every time the String supplied change it will be sent to the client and set by the provided Consumer.
     * @param supplier
     * @param setter
     * @return
     */
    public ContainerTileInventoryBuilder syncStringValue(final Supplier<String> supplier, final Consumer<String> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableString(supplier, setter));
        return this;
    }

    /**
     * Add a syncable FluidStack value to the container. Every time the FluidStack supplied change it will be sent to the client and set by the provided Consumer.
     * @param supplier
     * @param setter
     * @return
     */
    public ContainerTileInventoryBuilder syncFluidValue(final Supplier<FluidStack> supplier,
            final Consumer<FluidStack> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableFluid(supplier, setter));
        return this;
    }

    /**
     * Add a syncable ItemStack value to the container. Every time the ItemStack supplied change it will be sent to the client and set by the provided Consumer.
     * @param supplier
     * @param setter
     * @return
     */
    public ContainerTileInventoryBuilder syncItemValue(final Supplier<ItemStack> supplier,
            final Consumer<ItemStack> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableItem(supplier, setter));
        return this;
    }
    
    /**
     * WORK IN PROGRESS may absolutely not work as intended.
     * @param onCraft
     * @return
     */
    public ContainerTileInventoryBuilder onCraft(final Consumer<InventoryCrafting> onCraft)
    {
        this.parent.craftEvents.add(onCraft);
        return this;
    }

    /**
     * Close this builder and return the parent one.
     * @return
     */
    public ContainerBuilder addInventory()
    {
        this.parent.tileInventoryRanges.add(Range.between(this.rangeStart, this.parent.slots.size() - 1));
        this.parent.inventories.add(this.tile);
        return this.parent;
    }
}
