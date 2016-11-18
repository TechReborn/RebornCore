package reborncore.common.util.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import reborncore.common.tile.TileBase;
import reborncore.common.util.IInventoryUpdateable;

public class Inventory implements IInventory
{

	private final String inventoryName;
	private final int inventorySize;
	private final int inventoryStackLimit;
	private final ItemStack[] inventoryContent;

	private TileBase tileBase;

	public boolean isDirty;

	public Inventory(String inventoryName, int inventorySize, int inventoryStackLimit, TileBase tileBase) {
		this.inventoryName = inventoryName;
		this.inventorySize = inventorySize;
		this.inventoryStackLimit = inventoryStackLimit;
		this.inventoryContent = new ItemStack[this.getSizeInventory()];
		for (int i = 0; i < getSizeInventory(); i++) {
			inventoryContent[i] = ItemStack.EMPTY;
		}

		this.tileBase = tileBase;
	}

	@Override
	public int getSizeInventory() {
		return this.inventorySize;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : inventoryContent) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slotId) {
		return this.inventoryContent[slotId];
	}

	@Override
	public ItemStack decrStackSize(int slotId, int count) {
		if(slotId < this.getSizeInventory()) {
			ItemStack stack = this.getStackInSlot(slotId);
			if(stack != ItemStack.EMPTY && stack.getCount() > count) {
				ItemStack result = stack.splitStack(count);
				markDirty();
				return result;
			}

			setInventorySlotContents(slotId, ItemStack.EMPTY);
			return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack itemstack) {
		if(slotId < this.getSizeInventory()) {
			this.inventoryContent[slotId] = itemstack;

			if (itemstack != ItemStack.EMPTY && itemstack.getCount() > this.getInventoryStackLimit()) {
				itemstack.setCount(this.getInventoryStackLimit());
			}
			markDirty();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return this.inventoryStackLimit;
	}

	@Override
	public void markDirty() {
		if(this.tileBase instanceof IInventoryUpdateable) {
			((IInventoryUpdateable) this.tileBase).updateInventory();
		}
		this.tileBase.markBlockForUpdate();
		isDirty = true;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	public void readFromNBT(NBTTagCompound data) {
		NBTTagList nbttaglist = data.getTagList("inventory_" + getName(), Constants.NBT.TAG_COMPOUND);

		for(int i=0; i<nbttaglist.tagCount(); i++) {
			NBTTagCompound slot = nbttaglist.getCompoundTagAt(i);
			int index;
			if (slot.hasKey("index")) {
				index = slot.getInteger("index");
			}
			else {
				index = slot.getByte("Slot");
			}

			if (index >= 0 && index < this.getSizeInventory()) {
				this.setInventorySlotContents(index, new ItemStack(slot));
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		NBTTagList slots = new NBTTagList();
		for(byte index=0; index<this.getSizeInventory(); index++) {
			ItemStack stack = this.getStackInSlot(index);
			if (stack != ItemStack.EMPTY && stack.getCount() > 0) {
				NBTTagCompound slot = new NBTTagCompound();
				slots.appendTag(slot);
				slot.setByte("Slot", index);
				stack.writeToNBT(slot);
			}
		}
		data.setTag("inventory_" + getName(), slots);
		return data;
	}

	@Override
	public ItemStack removeStackFromSlot(int slotId) {
		if (slotId >= this.getSizeInventory() || this.getStackInSlot(slotId) == ItemStack.EMPTY) {
			return ItemStack.EMPTY;
		}

		ItemStack stackToTake = this.getStackInSlot(slotId);
		this.setInventorySlotContents(slotId, ItemStack.EMPTY);
		return stackToTake;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	// Custom fields that are being synced to the player :P
	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for(int i=0; i<this.getSizeInventory(); i++) {
			this.inventoryContent[i] = ItemStack.EMPTY;
		}
	}

	@Override
	public String getName() {
		return this.inventoryName;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}
}
