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

    public ContainerTileInventoryBuilder slot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new ListenerSlot(this.tile, index, x, y));
        return this;
    }

    public ContainerTileInventoryBuilder outputSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new SlotOutput(this.tile, index, x, y));
        return this;
    }

    public ContainerTileInventoryBuilder filterSlot(final int index, final int x, final int y,
            final Predicate<ItemStack> filter)
    {
        this.parent.slots.add(new FilteredSlot(this.tile, index, x, y).setFilter(filter));
        return this;
    }

    @SuppressWarnings("null")
    public ContainerTileInventoryBuilder fluidSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new FilteredSlot(this.tile, index, x, y).setFilter(
                stack -> stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP)));
        return this;
    }

    public ContainerTileInventoryBuilder fuelSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new SlotFuel(this.tile, index, x, y));
        return this;
    }

    public ContainerTileInventoryBuilder syncBooleanValue(final Supplier<Boolean> supplier,
            final Consumer<Boolean> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableBoolean(supplier, setter));
        return this;
    }

    public ContainerTileInventoryBuilder syncIntegerValue(final Supplier<Integer> supplier,
            final Consumer<Integer> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableInteger(supplier, setter));
        return this;
    }

    public ContainerTileInventoryBuilder syncStringValue(final Supplier<String> supplier, final Consumer<String> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableString(supplier, setter));
        return this;
    }

    public ContainerTileInventoryBuilder syncFluidValue(final Supplier<FluidStack> supplier,
            final Consumer<FluidStack> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableFluid(supplier, setter));
        return this;
    }

    public ContainerTileInventoryBuilder syncItemValue(final Supplier<ItemStack> supplier,
            final Consumer<ItemStack> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableItem(supplier, setter));
        return this;
    }

    public ContainerTileInventoryBuilder onCraft(final Consumer<InventoryCrafting> onCraft)
    {
        this.parent.craftEvents.add(onCraft);
        return this;
    }

    public ContainerBuilder addInventory()
    {
        this.parent.tileInventoryRanges.add(Range.between(this.rangeStart, this.parent.slots.size() - 1));
        this.parent.inventories.add(this.tile);
        return this.parent;
    }
}
