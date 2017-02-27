package teamreborn.reborncore.container;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.Range;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import teamreborn.reborncore.container.slot.ListenerSlot;
import teamreborn.reborncore.container.sync.SyncableProperty;

public class ContainerBuilder
{

    private final String                    name;

    private final EntityPlayer              player;
    private Predicate<EntityPlayer>         canInteract = player -> true;

    final List<ListenerSlot>                slots;
    final List<Range<Integer>>              playerInventoryRanges, tileInventoryRanges;

    List<SyncableProperty<?>>               syncables;

    final List<Consumer<InventoryCrafting>> craftEvents;

    List<IInventory>                        inventories;

    public ContainerBuilder(final String name, final EntityPlayer player)
    {

        this.name = name;

        this.player = player;

        this.slots = new ArrayList<>();
        this.playerInventoryRanges = new ArrayList<>();
        this.tileInventoryRanges = new ArrayList<>();

        this.syncables = new ArrayList<>();

        this.craftEvents = new ArrayList<>();

        this.inventories = new ArrayList<>();
    }

    public ContainerBuilder interact(final Predicate<EntityPlayer> canInteract)
    {
        this.canInteract = canInteract;
        return this;
    }

    public ContainerPlayerInventoryBuilder player(final InventoryPlayer player)
    {
        return new ContainerPlayerInventoryBuilder(this, player);
    }

    public ContainerTileInventoryBuilder tile(final IInventory tile)
    {
        return new ContainerTileInventoryBuilder(this, tile);
    }

    void addPlayerInventoryRange(final Range<Integer> range)
    {
        this.playerInventoryRanges.add(range);
    }

    void addTileInventoryRange(final Range<Integer> range)
    {
        this.tileInventoryRanges.add(range);
    }

    public BuiltContainer create()
    {
        final BuiltContainer built = new BuiltContainer(this.name, this.player, this.inventories, this.canInteract,
                this.playerInventoryRanges, this.tileInventoryRanges);
        if (!this.syncables.isEmpty())
            built.setSyncables(this.syncables);
        if (!this.craftEvents.isEmpty())
            built.addCraftEvents(this.craftEvents);

        this.slots.forEach(built::addSlot);

        this.slots.clear();
        return built;
    }
}
