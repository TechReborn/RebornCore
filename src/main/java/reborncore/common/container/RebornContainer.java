package reborncore.common.container;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import reborncore.RebornCore;
import reborncore.api.tile.IContainerLayout;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotFake;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import java.lang.reflect.InvocationTargetException;

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
		return createContainer(clazz, tileEntity, RebornCore.proxy.getPlayer());
	}

	public static RebornContainer createContainer(Class<? extends RebornContainer> clazz, TileEntity tileEntity, EntityPlayer player){
		if(player == null && containerMap.containsKey(clazz.getCanonicalName())){
			return containerMap.get(clazz.getCanonicalName());
		} else {
			try {
				RebornContainer container = null;
				for(Constructor constructor : clazz.getConstructors()){
					if(constructor.getParameterCount() == 0){
						container = clazz.newInstance();
						if(container instanceof IContainerLayout){
							((IContainerLayout) container).setTile(tileEntity);
							((IContainerLayout) container).addInventorySlots();
						}
						continue;
					} else if (constructor.getParameterCount() == 2){
						Class[] paramTypes = constructor.getParameterTypes();
						if(paramTypes[0].isInstance(tileEntity) && paramTypes[1] == EntityPlayer.class){
							container = clazz.getDeclaredConstructor(tileEntity.getClass(), EntityPlayer.class).newInstance(tileEntity, player);
							continue;
						} else if (paramTypes[0] == EntityPlayer.class && paramTypes[1].isInstance(tileEntity)){
							container = clazz.getDeclaredConstructor(EntityPlayer.class, tileEntity.getClass()).newInstance(player, tileEntity);
							continue;
						}
					}
				}
				if(container == null){
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
					return ItemStack.field_190927_a;
				}
			} else if (slotIndex >= numSlots - 9 && slotIndex < numSlots)
			{
				if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9))
				{
					return ItemStack.field_190927_a;
				}
			} else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots))
			{
				return ItemStack.field_190927_a;
			}
			slot.onSlotChange(stackInSlot, originalStack);
			if (stackInSlot.func_190916_E() <= 0)
			{
				slot.putStack(ItemStack.field_190927_a);
			} else
			{
				slot.onSlotChanged();
			}
			if (stackInSlot.func_190916_E() == originalStack.func_190916_E())
			{
				return ItemStack.field_190927_a;
			}
			slot.func_190901_a(player, stackInSlot);
		}
		return originalStack;
	}

	protected boolean shiftItemStack(ItemStack stackToShift, int start, int end)
	{
		boolean changed = false;
		if (stackToShift.isStackable())
		{
			for (int slotIndex = start; stackToShift.func_190916_E() > 0 && slotIndex < end; slotIndex++)
			{
				Slot slot = (Slot) inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot != ItemStack.field_190927_a && canStacksMerge(stackInSlot, stackToShift))
				{
					int resultingStackSize = stackInSlot.func_190916_E()  + stackToShift.func_190916_E() ;
					int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					if (resultingStackSize <= max)
					{
						stackToShift.func_190920_e(0);
						stackInSlot.func_190920_e(resultingStackSize);
						slot.onSlotChanged();
						changed = true;
					} else if (stackInSlot.func_190916_E() < max)
					{
						stackToShift.func_190920_e(-(max - stackInSlot.func_190916_E()));
						stackInSlot.func_190920_e(max);
						slot.onSlotChanged();
						changed = true;
					}
				}
			}
		}
		if (stackToShift.func_190916_E() > 0)
		{
			for (int slotIndex = start; stackToShift.func_190916_E() > 0 && slotIndex < end; slotIndex++)
			{
				Slot slot = (Slot) inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot == ItemStack.field_190927_a)
				{
					int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					stackInSlot = stackToShift.copy();
					stackInSlot.func_190920_e(Math.min(stackToShift.func_190916_E(), max));
					stackToShift.func_190920_e(-stackInSlot.func_190916_E());
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
		if (stack1 == ItemStack.field_190927_a || stack2 == ItemStack.field_190927_a)
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
