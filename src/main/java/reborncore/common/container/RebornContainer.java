/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.common.container;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.RebornCore;
import reborncore.api.tile.IContainerLayout;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotFake;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class RebornContainer extends Container {
	private static HashMap<String, RebornContainer> containerMap = new HashMap<>();
	public HashMap<Integer, BaseSlot> slotMap = new HashMap<>();

	private final BlockEntity baseTile;

	Component title;

	public RebornContainer(BlockEntity tileEntity, Component title) {
		super(null, 0);
		this.baseTile = tileEntity;
		this.title = title;
	}

	public Component getTitle() {
		return title;
	}

	public static
	@Nullable
	RebornContainer getContainerFromClass(Class<? extends RebornContainer> clazz, BlockEntity tileEntity) {
		return createContainer(clazz, tileEntity, RebornCore.proxy.getPlayer());
	}

	public static RebornContainer createContainer(Class<? extends RebornContainer> clazz, BlockEntity tileEntity, PlayerEntity player) {
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
						if (paramTypes[0].isInstance(tileEntity) && paramTypes[1] == PlayerEntity.class) {
							container = clazz.getDeclaredConstructor(tileEntity.getClass(), PlayerEntity.class).newInstance(tileEntity, player);
							continue;
						} else if (paramTypes[0] == PlayerEntity.class && paramTypes[1].isInstance(tileEntity)) {
							container = clazz.getDeclaredConstructor(PlayerEntity.class, tileEntity.getClass()).newInstance(player, tileEntity);
							continue;
						}
					}
				}
				if (container == null) {
					RebornCore.LOGGER.error("Failed to create container for " + clazz.getName() + " bad things may happen, please report to devs");
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
		if (!stack1.isItemEqualIgnoreDamage(stack2)) {
			return false;
		}
		if (!ItemStack.areTagsEqual(stack1, stack2)) {
			return false;
		}
		return true;

	}

	@Override
	protected Slot addSlot(Slot slotIn) {
		Slot slot = super.addSlot(slotIn);
		if (slot instanceof BaseSlot) {
			//TODO remove player slots
			slotMap.put(slot.id, (BaseSlot) slot);
		}
		return slot;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int slotIndex) {
		ItemStack originalStack = ItemStack.EMPTY;
		Slot slot = (Slot) slotList.get(slotIndex);
		int numSlots = slotList.size();
		if (slot != null && slot.hasStack()) {
			ItemStack stackInSlot = slot.getStack();
			originalStack = stackInSlot.copy();
			if (slotIndex >= numSlots - 9 * 4 && tryShiftItem(stackInSlot, numSlots)) {
				// NOOP
			} else if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9) {
				if (!shiftItemStack(stackInSlot, numSlots - 9, numSlots)) {
					return ItemStack.EMPTY;
				}
			} else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
				if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9)) {
					return ItemStack.EMPTY;
				}
			} else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots)) {
				return ItemStack.EMPTY;
			}
			slot.onStackChanged(stackInSlot, originalStack);
			if (stackInSlot.getCount() <= 0) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
			if (stackInSlot.getCount() == originalStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTakeItem(player, stackInSlot);
		}
		return originalStack;
	}

	protected boolean shiftItemStack(ItemStack stackToShift, int start, int end) {
		boolean changed = false;
		if (stackToShift.isStackable()) {
			for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
				Slot slot = (Slot) slotList.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (!stackInSlot.isEmpty() && canStacksMerge(stackInSlot, stackToShift)) {
					int resultingStackSize = stackInSlot.getCount() + stackToShift.getCount();
					int max = Math.min(stackToShift.getMaxCount(), slot.getMaxStackAmount());
					if (resultingStackSize <= max) {
						stackToShift.setCount(0);
						stackInSlot.setCount(resultingStackSize);
						slot.markDirty();
						changed = true;
					} else if (stackInSlot.getCount() < max) {
						stackToShift.setCount(stackToShift.getCount() - (max - stackInSlot.getCount()));
						stackInSlot.setCount(max);
						slot.markDirty();
						changed = true;
					}
				}
			}
		}
		if (stackToShift.getCount() > 0) {
			for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
				Slot slot = (Slot) slotList.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot.isEmpty()) {
					int max = Math.min(stackToShift.getMaxCount(), slot.getMaxStackAmount());
					stackInSlot = stackToShift.copy();
					stackInSlot.setCount(Math.min(stackToShift.getCount(), max));
					stackToShift.setCount(stackToShift.getCount() - stackInSlot.getCount());
					slot.setStack(stackInSlot);
					slot.markDirty();
					changed = true;
				}
			}
		}
		return changed;
	}

	private boolean tryShiftItem(ItemStack stackToShift, int numSlots) {
		for (int machineIndex = 0; machineIndex < numSlots - 9 * 4; machineIndex++) {
			Slot slot = (Slot) slotList.get(machineIndex);
			if (slot instanceof SlotFake) {
				continue;
			}
			if (!slot.canInsert(stackToShift)) {
				continue;
			}
			if (shiftItemStack(stackToShift, machineIndex, machineIndex + 1)) {
				return true;
			}
		}
		return false;
	}

	public void addPlayersHotbar(PlayerEntity player) {
		int i;
		for (i = 0; i < 9; ++i) {
			this.addSlot(new Slot(player.inventory, i, 8 + i * 18, 142));
		}
	}

	public void addPlayersInventory(PlayerEntity player) {
		int i;
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
	}

	public void drawPlayersInv(PlayerEntity player) {
		drawPlayersInv(player, 8, 81);
		//		int i;
		//		for (i = 0; i < 3; ++i)
		//        {
		//			for (int j = 0; j < 9; ++j)
		//            {
		//				this.addSlotToContainer(new BaseSlot(player.inventory, j + i * 9 + 9, 8 + j * 18, 81 + i * 18));
		//			}
		//		}

	}

	public void drawPlayersHotBar(PlayerEntity player) {
		drawPlayersHotBar(player, 8, 139);
		//		int i;
		//		for (i = 0; i < 9; ++i)
		//        {
		//			this.addSlotToContainer(new BaseSlot(player.inventory, i, 8 + i * 18, 139));
		//		}
	}

	public void drawPlayersInv(PlayerEntity player, int x, int y) {
		int i;
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(player.inventory, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}

	}

	public void drawPlayersHotBar(PlayerEntity player, int x, int y) {
		int i;
		for (i = 0; i < 9; ++i) {
			this.addSlot(new Slot(player.inventory, i, x + i * 18, y));
		}
	}

	public void drawPlayersInvAndHotbar(PlayerEntity player) {
		drawPlayersInv(player);
		drawPlayersHotBar(player);
	}

	public void drawPlayersInvAndHotbar(PlayerEntity player, int x, int y) {
		drawPlayersInv(player, x, y);
		drawPlayersHotBar(player, x, y + 58);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		if (baseTile != null) {
			World world = player.getEntityWorld();
			BlockPos pos = baseTile.getPos();
			return world.getBlockEntity(pos) == baseTile && player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
		}
		return true;
	}
}
