/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.client.containerBuilder.builder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import reborncore.client.containerBuilder.IRightClickHandler;
import reborncore.client.gui.slots.SlotFake;
import reborncore.common.tile.TileLegacyMachineBase;
import reborncore.common.util.ItemUtils;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.*;

public class BuiltContainer extends Container implements IExtendedContainerListener {

    private final String name;

    private final Predicate<EntityPlayer> canInteract;


    private final ArrayList<MutableTriple<IntSupplier, IntConsumer, Short>> shortValues;
    private final ArrayList<MutableTriple<IntSupplier, IntConsumer, Integer>> integerValues;
    private final ArrayList<MutableTriple<LongSupplier, LongConsumer, Long>> longValues;
    private final ArrayList<MutableTriple<Supplier<FluidStack>, Consumer<FluidStack>, FluidStack>> fluidStackValues;
    private final ArrayList<MutableTriple<Supplier, Consumer, Object>> objectValues;
    private List<Consumer<InventoryCrafting>> craftEvents;
    private Integer[] integerParts;

    private final TileLegacyMachineBase tile;

    public BuiltContainer(final String name, final Predicate<EntityPlayer> canInteract, TileLegacyMachineBase tile) {
        this.name = name;

        this.canInteract = canInteract;
        this.shortValues = new ArrayList<>();
        this.integerValues = new ArrayList<>();
        this.longValues = new ArrayList<>();
        this.fluidStackValues = new ArrayList<>();
        this.objectValues = new ArrayList<>();

        this.tile = tile;
    }

    public void addShortSync(final List<Pair<IntSupplier, IntConsumer>> syncables) {
        for (final Pair<IntSupplier, IntConsumer> syncable : syncables)
            this.shortValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), (short) 0));
        this.shortValues.trimToSize();
    }

    public void addLongSync(final List<Pair<LongSupplier, LongConsumer>> syncables) {
        for (final Pair<LongSupplier, LongConsumer> syncable : syncables)
            this.longValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), (long) 0));
        this.longValues.trimToSize();
    }

    public void addIntegerSync(final List<Pair<IntSupplier, IntConsumer>> syncables) {
        for (final Pair<IntSupplier, IntConsumer> syncable : syncables)
            this.integerValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), 0));
        this.integerValues.trimToSize();
        this.integerParts = new Integer[this.integerValues.size()];
    }

    public void addFluidStackSync(final List<Pair<Supplier<FluidStack>, Consumer<FluidStack>>> syncables) {
        for (final Pair<Supplier<FluidStack>, Consumer<FluidStack>> syncable : syncables)
            this.fluidStackValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), null));
        this.fluidStackValues.trimToSize();
    }

    public void addObjectSync(final List<Pair<Supplier, Consumer>> syncables) {
        for (final Pair<Supplier, Consumer> syncable : syncables)
            this.objectValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), null));
        this.objectValues.trimToSize();
    }

    public void addCraftEvents(final List<Consumer<InventoryCrafting>> craftEvents) {
        this.craftEvents = craftEvents;
    }

    public void addSlot(final Slot slot) {
        this.addSlotToContainer(slot);
    }

    @Override
    public boolean canInteractWith(final EntityPlayer playerIn) {
        return this.tile != null
                ? tile.isUsableByPlayer(playerIn)
                : this.canInteract.test(playerIn);
    }

    @Override
    public final void onCraftMatrixChanged(final IInventory inv) {
        if (!this.craftEvents.isEmpty())
            this.craftEvents.forEach(consumer -> consumer.accept((InventoryCrafting) inv));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (dragType == 1 && slotId > 0 && slotId < 1000) {
            Slot slot = this.inventorySlots.get(slotId);
            if (slot instanceof IRightClickHandler) {
                if (((IRightClickHandler) slot).handleRightClick(slot.getSlotIndex(), player, this)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (final IContainerListener listener : this.listeners) {

            int i = 0;
            if (!this.shortValues.isEmpty())
                for (final MutableTriple<IntSupplier, IntConsumer, Short> value : this.shortValues) {
                    final short supplied = (short) value.getLeft().getAsInt();
                    if (supplied != value.getRight()) {

                        listener.sendWindowProperty(this, i, supplied);
                        value.setRight(supplied);
                    }
                    i++;
                }

            if (!this.integerValues.isEmpty())
                for (final MutableTriple<IntSupplier, IntConsumer, Integer> value : this.integerValues) {
                    final int supplied = value.getLeft().getAsInt();
                    if (supplied != value.getRight()) {

                        listener.sendWindowProperty(this, i, supplied >> 16);
                        listener.sendWindowProperty(this, i + 1, (short) (supplied & 0xFFFF));
                        value.setRight(supplied);
                    }
                    i += 2;
                }

            if (!this.longValues.isEmpty()) {
                int longs = 0;
                for (final MutableTriple<LongSupplier, LongConsumer, Long> value : this.longValues) {
                    final long supplied = value.getLeft().getAsLong();
                    if (supplied != value.getRight()) {
                        sendLong(listener, this, longs, supplied);
                        value.setRight(supplied);
                    }
                    longs++;
                }
            }

            if (!this.fluidStackValues.isEmpty()) {
                int fluidStacks = 0;
                for (final MutableTriple<Supplier<FluidStack>, Consumer<FluidStack>, FluidStack> value : this.fluidStackValues) {
                    final FluidStack supplied = value.getLeft().get();
                    sendFluidStack(listener, this, fluidStacks, supplied);
//                        if (listener instanceof EntityPlayerMP) {
//                            PacketHandler.sendTo(new VariableMessage(VariableMessage.Type.GUI, this.tile.getWorld().provider.getDimension(),
//                                    this.tile.getPos(), supplied.writeToNBT(new NBTTagCompound())), (EntityPlayerMP) listener);
//                        }
                    value.setRight(supplied);
                    fluidStacks++;
                }
            }

            if (!this.objectValues.isEmpty()) {
                int objects = 0;
                for (final MutableTriple<Supplier, Consumer, Object> value : this.objectValues) {
                    final Object supplied = value.getLeft();
                    if (((Supplier) supplied).get() != value.getRight()) {
                        sendObject(listener, this, objects, ((Supplier) supplied).get());
                        value.setRight(((Supplier) supplied).get());
                    }
                    objects++;
                }
            }
        }
    }

    @Override
    public void addListener(final IContainerListener listener) {
        super.addListener(listener);

        int i = 0;
        if (!this.shortValues.isEmpty())
            for (final MutableTriple<IntSupplier, IntConsumer, Short> value : this.shortValues) {
                final short supplied = (short) value.getLeft().getAsInt();

                listener.sendWindowProperty(this, i, supplied);
                value.setRight(supplied);
                i++;
            }

        if (!this.integerValues.isEmpty())
            for (final MutableTriple<IntSupplier, IntConsumer, Integer> value : this.integerValues) {
                final int supplied = value.getLeft().getAsInt();

                listener.sendWindowProperty(this, i, supplied >> 16);
                listener.sendWindowProperty(this, i + 1, (short) (supplied & 0xFFFF));
                value.setRight(supplied);
                i += 2;
            }

        if (!this.longValues.isEmpty()) {
            int longs = 0;
            for (final MutableTriple<LongSupplier, LongConsumer, Long> value : this.longValues) {
                final long supplied = value.getLeft().getAsLong();
                sendLong(listener, this, longs, supplied);
                value.setRight(supplied);
                longs++;
            }
        }

        if (!this.fluidStackValues.isEmpty()) {
            int fluidStacks = 0;
            for (final MutableTriple<Supplier<FluidStack>, Consumer<FluidStack>, FluidStack> value : this.fluidStackValues) {
                final FluidStack supplied = value.getLeft().get();
                sendFluidStack(listener, this, fluidStacks, supplied);
                value.setRight(supplied);
                fluidStacks++;
            }
        }

        if (!this.objectValues.isEmpty()) {
            int objects = 0;
            for (final MutableTriple<Supplier, Consumer, Object> value : this.objectValues) {
                final Object supplied = value.getLeft();
                sendObject(listener, this, objects, ((Supplier) supplied).get());
                value.setRight(supplied);
                objects++;
            }
        }
    }

    @Override
    public void handleLong(int var, long value) {
        this.longValues.get(var).getMiddle().accept(value);
    }

    @Override
    public void handleFluidStack(int var, FluidStack value) {
        this.fluidStackValues.get(var).getMiddle().accept(value);
    }

    @Override
    public void handleObject(int var, Object value) {
        this.objectValues.get(var).getMiddle().accept(value);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(final int id, final int value) {
        if (id < this.shortValues.size()) {
            this.shortValues.get(id).getMiddle().accept((short) value);
            this.shortValues.get(id).setRight((short) value);
        } else if (id - this.shortValues.size() < this.integerValues.size() * 2) {

            if ((id - this.shortValues.size()) % 2 == 0)
                this.integerParts[(id - this.shortValues.size()) / 2] = value;
            else {
                this.integerValues.get((id - this.shortValues.size()) / 2).getMiddle().accept(
                        (this.integerParts[(id - this.shortValues.size()) / 2] & 0xFFFF) << 16 | value & 0xFFFF);
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex) {
        Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        if (sourceSlot != null && sourceSlot.getHasStack()) {
            ItemStack sourceItemStack = sourceSlot.getStack();
            int oldSourceItemStackSize = ItemUtils.getSize(sourceItemStack);

            ItemStack destinationStack = sourceSlot.inventory == player.inventory
                    ? handlePlayerSlotShiftClick(player, sourceItemStack) // Player inventory clicked
                    : handleGUISlotShiftClick(player, sourceItemStack); // GUI inventory clicked

            if (ItemUtils.isEmpty(destinationStack) || ItemUtils.getSize(destinationStack) != oldSourceItemStackSize) {
                sourceSlot.putStack(destinationStack);
                sourceSlot.onTake(player, sourceItemStack);

                if (!player.getEntityWorld().isRemote) detectAndSendChanges(); // Force a re-sync
            }
        }

        return ItemStack.EMPTY;
    }

    // Getters & Setters >>
    public String getName() {
        return this.name;
    }
    // << Getters & Setters

    // Helper methods >>
    protected ItemStack handlePlayerSlotShiftClick(EntityPlayer player, ItemStack sourceItemStack) {
        // Passes:
        // 0: fill input existing stacks
        // 1: fill input empty stacks
        // 2: fill existing stacks
        // 3: fill empty stacks
        for (int pass = 0; pass < 4 && !ItemUtils.isEmpty(sourceItemStack); pass++) {
            for (Slot targetSlot : inventorySlots) {
                if (targetSlot.inventory != player.inventory
                        && isValidTargetSlot(targetSlot, sourceItemStack, pass % 2 == 1, pass < 2)) {
                    sourceItemStack = transfer(sourceItemStack, targetSlot);

                    if (ItemUtils.isEmpty(sourceItemStack)) break;
                }
            }
        }

        return sourceItemStack;
    }

    protected ItemStack handleGUISlotShiftClick(EntityPlayer player, ItemStack sourceItemStack) {
        for (int pass = 0; pass < 2 && !ItemUtils.isEmpty(sourceItemStack); pass++) {
            ListIterator<Slot> it = inventorySlots.listIterator(inventorySlots.size());
            while (it.hasPrevious()) {
                Slot targetSlot = it.previous();
                if (targetSlot.inventory == player.inventory
                        && isValidTargetSlot(targetSlot, sourceItemStack, pass == 1, false)) {
                    sourceItemStack = transfer(sourceItemStack, targetSlot);

                    if (ItemUtils.isEmpty(sourceItemStack)) break;
                }
            }
        }

        return sourceItemStack;
    }

    protected static boolean isValidTargetSlot(Slot slot, ItemStack itemStack, boolean allowEmpty, boolean requireInputOnly) {
        if (slot instanceof SlotFake) return false;

        if (!slot.isItemValid(itemStack)) return false;

        if (!allowEmpty && !slot.getHasStack()) return false;

        return !requireInputOnly || slot.isItemValid(itemStack);
    }

    protected static ItemStack transfer(ItemStack itemStack, Slot destination) {
        int amount = getTransferAmount(itemStack, destination);
        if (amount <= 0) return itemStack;

        ItemStack destinationStack = destination.getStack();
        destination.putStack(ItemUtils.isEmpty(destinationStack)
                ? ItemUtils.copyWithSize(itemStack, amount)
                : ItemUtils.increaseSize(destinationStack, amount));

        return ItemUtils.decreaseSize(itemStack, amount);
    }

    protected static int getTransferAmount(ItemStack itemStack, Slot destination) {
        int maxAmount = Math.min(destination.inventory.getInventoryStackLimit(), destination.getSlotStackLimit());
        maxAmount = Math.min(maxAmount, itemStack.isStackable() ? itemStack.getMaxStackSize() : 1);

        ItemStack destinationStack = destination.getStack();
        if (!ItemUtils.isEmpty(destinationStack)) {
            if (!ItemUtils.isItemEqual(itemStack, destinationStack, true, true)) return 0;

            maxAmount -= ItemUtils.getSize(destinationStack);
        }

        return Math.min(maxAmount, ItemUtils.getSize(itemStack));
    }
    // << Helper methods
}
