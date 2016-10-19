package reborncore.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import reborncore.api.tile.IContainerLayout;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotFake;

import javax.annotation.Nullable;
import java.util.HashMap;

public abstract class RebornContainer extends Container
{
	public HashMap<Integer, BaseSlot> slotMap = new HashMap<>();

	@Override
	protected Slot addSlotToContainer(Slot slotIn) {
		Slot slot = super.addSlotToContainer(slotIn);
		if(slot instanceof BaseSlot){
			//TODO remove player slots
			slotMap.put(slot.getSlotIndex(), (BaseSlot) slot);
		}
		return slot;
	}

	private static HashMap<String, RebornContainer> containerMap = new HashMap<>();

	public static @Nullable RebornContainer getContainerFromClass(Class<? extends RebornContainer> clazz, TileEntity tileEntity){
		if(containerMap.containsKey(clazz.getCanonicalName())){
			return containerMap.get(clazz.getCanonicalName());
		} else {
			try {
				//TODO think hard about how to fix this one
				RebornContainer container = clazz.newInstance();
				if(container instanceof IContainerLayout){
					((IContainerLayout) container).setTile(tileEntity);
					((IContainerLayout) container).addInventorySlots();
				}
				containerMap.put(clazz.getCanonicalName(), container);
				return container;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static RebornContainer createContainer(Class<? extends RebornContainer> clazz, TileEntity tile, EntityPlayer player){
		try {
			RebornContainer container = clazz.newInstance();
			if(container instanceof IContainerLayout){
				((IContainerLayout) container).setPlayer(player);
				((IContainerLayout) container).setTile(tile);
				((IContainerLayout) container).addInventorySlots();
				((IContainerLayout) container).addPlayerSlots();
			}
			return container;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		ItemStack originalStack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);
		int numSlots = inventorySlots.size();
		if (slot != null && slot.getHasStack())
		{
			ItemStack stackInSlot = slot.getStack();
			originalStack = stackInSlot.copy();
			if (slotIndex >= numSlots - 9 * 4 && tryShiftItem(stackInSlot, numSlots))
			{
				// NOOP
			} else if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9)
			{
				if (!shiftItemStack(stackInSlot, numSlots - 9, numSlots))
				{
					return null;
				}
			} else if (slotIndex >= numSlots - 9 && slotIndex < numSlots)
			{
				if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9))
				{
					return null;
				}
			} else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots))
			{
				return null;
			}
			slot.onSlotChange(stackInSlot, originalStack);
			if (stackInSlot.stackSize <= 0)
			{
				slot.putStack(null);
			} else
			{
				slot.onSlotChanged();
			}
			if (stackInSlot.stackSize == originalStack.stackSize)
			{
				return null;
			}
			slot.onPickupFromSlot(player, stackInSlot);
		}
		return originalStack;
	}

	protected boolean shiftItemStack(ItemStack stackToShift, int start, int end)
	{
		boolean changed = false;
		if (stackToShift.isStackable())
		{
			for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end; slotIndex++)
			{
				Slot slot = (Slot) inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot != null && canStacksMerge(stackInSlot, stackToShift))
				{
					int resultingStackSize = stackInSlot.stackSize + stackToShift.stackSize;
					int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					if (resultingStackSize <= max)
					{
						stackToShift.stackSize = 0;
						stackInSlot.stackSize = resultingStackSize;
						slot.onSlotChanged();
						changed = true;
					} else if (stackInSlot.stackSize < max)
					{
						stackToShift.stackSize -= max - stackInSlot.stackSize;
						stackInSlot.stackSize = max;
						slot.onSlotChanged();
						changed = true;
					}
				}
			}
		}
		if (stackToShift.stackSize > 0)
		{
			for (int slotIndex = start; stackToShift.stackSize > 0 && slotIndex < end; slotIndex++)
			{
				Slot slot = (Slot) inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot == null)
				{
					int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					stackInSlot = stackToShift.copy();
					stackInSlot.stackSize = Math.min(stackToShift.stackSize, max);
					stackToShift.stackSize -= stackInSlot.stackSize;
					slot.putStack(stackInSlot);
					slot.onSlotChanged();
					changed = true;
				}
			}
		}
		return changed;
	}

	private boolean tryShiftItem(ItemStack stackToShift, int numSlots)
	{
		for (int machineIndex = 0; machineIndex < numSlots - 9 * 4; machineIndex++)
		{
			Slot slot = (Slot) inventorySlots.get(machineIndex);
			if (slot instanceof SlotFake)
			{
				continue;
			}
			if (!slot.isItemValid(stackToShift))
				continue;
			if (shiftItemStack(stackToShift, machineIndex, machineIndex + 1))
				return true;
		}
		return false;
	}

	public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2)
	{
		if (stack1 == null || stack2 == null)
		{
			return false;
		}
		if (!stack1.isItemEqual(stack2))
		{
			return false;
		}
		if (!ItemStack.areItemStackTagsEqual(stack1, stack2))
		{
			return false;
		}
		return true;

	}

	public void addPlayersHotbar(EntityPlayer player)
	{
		int i;
		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
		}
	}

	public void addPlayersInventory(EntityPlayer player)
	{
		int i;
		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
	}

	public void drawPlayersInv(EntityPlayer player)
	{
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

	public void drawPlayersHotBar(EntityPlayer player)
	{
        drawPlayersHotBar(player, 8, 139);
//		int i;
//		for (i = 0; i < 9; ++i)
//        {
//			this.addSlotToContainer(new BaseSlot(player.inventory, i, 8 + i * 18, 139));
//		}
	}

	public void drawPlayersInv(EntityPlayer player, int x, int y)
	{
		int i;
		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new BaseSlot(player.inventory, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}

	}

	public void drawPlayersHotBar(EntityPlayer player, int x, int y)
	{
		int i;
		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new BaseSlot(player.inventory, i, x + i * 18, y));
		}
	}

	public void drawPlayersInvAndHotbar(EntityPlayer player)
	{
		drawPlayersInv(player);
		drawPlayersHotBar(player);
	}

    public void drawPlayersInvAndHotbar(EntityPlayer player, int x, int y)
    {
        drawPlayersInv(player, x, y);
        drawPlayersHotBar(player, x, y);
    }
}
