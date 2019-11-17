/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package reborncore.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import reborncore.RebornCore;
import reborncore.api.tile.IContainerLayout;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotFake;
import reborncore.common.util.ItemUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Optional;

public abstract class RebornContainer extends Container {
    private static HashMap<String, RebornContainer> containerMap = new HashMap<>();
    public HashMap<Integer, BaseSlot> slotMap = new HashMap<>();

    private Optional<TileEntity> baseTile = Optional.empty();

    @Deprecated //TODO remove in 1.13 to use tile senstive version
    public RebornContainer() {
    }

    public RebornContainer(TileEntity tileEntity) {
        this.baseTile = Optional.of(tileEntity);
    }

    public static
    @Nullable
    RebornContainer getContainerFromClass(Class<? extends RebornContainer> clazz, TileEntity tileEntity) {
        return createContainer(clazz, tileEntity, RebornCore.proxy.getPlayer());
    }

    public static RebornContainer createContainer(Class<? extends RebornContainer> clazz, TileEntity tileEntity, EntityPlayer player) {
        if (player == null && containerMap.containsKey(clazz.getCanonicalName())) {
            return containerMap.get(clazz.getCanonicalName());
        } else {
            try {
                RebornContainer container = null;
                for (Constructor constructor : clazz.getConstructors()) {
                    if (constructor.getParameterCount() == 0) {
                        container = clazz.newInstance();
                        if (container instanceof IContainerLayout) {
                            ((IContainerLayout) container).setTile(tileEntity);
                            ((IContainerLayout) container).addInventorySlots();
                        }
                        continue;
                    } else if (constructor.getParameterCount() == 2) {
                        Class[] paramTypes = constructor.getParameterTypes();
                        if (paramTypes[0].isInstance(tileEntity) && paramTypes[1] == EntityPlayer.class) {
                            container = clazz.getDeclaredConstructor(tileEntity.getClass(), EntityPlayer.class).newInstance(tileEntity, player);
                            continue;
                        } else if (paramTypes[0] == EntityPlayer.class && paramTypes[1].isInstance(tileEntity)) {
                            container = clazz.getDeclaredConstructor(EntityPlayer.class, tileEntity.getClass()).newInstance(player, tileEntity);
                            continue;
                        }
                    }
                }
                if (container == null) {
                    RebornCore.logHelper.error("Failed to create container for " + clazz.getName() + " bad things may happen, please report to devs");
                }
                containerMap.put(clazz.getCanonicalName(), container);
                return container;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() || stack2.isEmpty()) {
            return false;
        }
        if (!stack1.isItemEqual(stack2)) {
            return false;
        }
        if (!ItemStack.areItemStackTagsEqual(stack1, stack2)) {
            return false;
        }
        return true;

    }

    @Override
    protected Slot addSlotToContainer(Slot slotIn) {
        Slot slot = super.addSlotToContainer(slotIn);
        if (slot instanceof BaseSlot) {
            //TODO remove player slots
            slotMap.put(slot.getSlotIndex(), (BaseSlot) slot);
        }
        return slot;
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

    public void drawPlayersInv(EntityPlayer player, int x, int y) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new BaseSlot(player.inventory, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }

    }

    public void drawPlayersHotBar(EntityPlayer player, int x, int y) {
        int i;
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new BaseSlot(player.inventory, i, x + i * 18, y));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (baseTile.isPresent()) {
            World world = player.getEntityWorld();
            BlockPos pos = baseTile.get().getPos();
            return world.getTileEntity(pos) == baseTile.get() && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
        }
        return true;
    }

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
