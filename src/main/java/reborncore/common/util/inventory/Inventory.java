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

		this.tileBase = tileBase;
	}

	@Override
	public int getSizeInventory() {
		return this.inventorySize;
	}

	@Override
	public ItemStack getStackInSlot(int slotId) {
		return this.inventoryContent[slotId];
	}

	@Override
	public ItemStack decrStackSize(int slotId, int count) {
		if(slotId < this.getSizeInventory()) {
			ItemStack stack = this.getStackInSlot(slotId);
			if(stack != null && stack.stackSize > count) {
				ItemStack result = stack.splitStack(count);
				markDirty();
				return result;
			}

			setInventorySlotContents(slotId, null);
			return stack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack itemstack) {
		if(slotId < this.getSizeInventory()) {
			this.inventoryContent[slotId] = itemstack;

			if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
				itemstack.stackSize = this.getInventoryStackLimit();
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
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
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
				this.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(slot));
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		NBTTagList slots = new NBTTagList();
		for(byte index=0; index<this.getSizeInventory(); index++) {
			ItemStack stack = this.getStackInSlot(index);
			if (stack != null && stack.stackSize > 0) {
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
		if (slotId >= this.getSizeInventory() || this.getStackInSlot(slotId) == null) {
			return null;
		}

		ItemStack stackToTake = this.getStackInSlot(slotId);
		this.setInventorySlotContents(slotId, null);
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
			this.inventoryContent[i] = null;
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

//	public final ItemStack[] contents;
//	private final String name;
//	private final int stackLimit;
//	private IInventoryUpdateable tile;
//
//	public Inventory(int size, String name, int stackLimit)
//	{
//		this.contents = new ItemStack[size];
//		this.name = name;
//		this.stackLimit = stackLimit;
//	}
//
//	public Inventory(int size, String name, int stackLimit, IInventoryUpdateable tile)
//	{
//		this(size, name, stackLimit);
//		this.tile = tile;
//	}
//
//	@Override
//	public int getSizeInventory()
//	{
//		return contents.length;
//	}
//
//	@Override
//	public ItemStack getStackInSlot(int slotId)
//	{
//		return contents[slotId];
//	}
//
//	@Override
//	public ItemStack decrStackSize(int slotId, int count)
//	{
//		if (slotId < contents.length && contents[slotId] != null)
//		{
//			if (contents[slotId].stackSize > count)
//			{
//				ItemStack result = contents[slotId].splitStack(count);
//				markDirty();
//				return result;
//			}
//			ItemStack stack = contents[slotId];
//			setInventorySlotContents(slotId, null);
//			return stack;
//		}
//		return null;
//	}
//
//	@Override
//	public void setInventorySlotContents(int slotId, ItemStack itemstack)
//	{
//		if (slotId >= contents.length)
//		{
//			return;
//		}
//		contents[slotId] = itemstack;
//
//		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
//		{
//			itemstack.stackSize = this.getInventoryStackLimit();
//		}
//		markDirty();
//	}
//
//	@Override
//	public int getInventoryStackLimit()
//	{
//		return stackLimit;
//	}
//
//	@Override
//	public void markDirty() {
//		if(this.tile == null)
//			return;
//
//		this.tile.updateInventory();
//	}
//
//	@Override
//	public boolean isUseableByPlayer(EntityPlayer entityplayer)
//	{
//		return true;
//	}
//
//	@Override
//	public void openInventory(EntityPlayer player)
//	{
//
//	}
//
//	@Override
//	public void closeInventory(EntityPlayer player)
//	{
//
//	}
//
//	public void readFromNBT(NBTTagCompound data)
//	{
//		readFromNBT(data, "Items");
//	}
//
//	public void readFromNBT(NBTTagCompound data, String tag)
//	{
//		NBTTagList nbttaglist = data.getTagList(tag, Constants.NBT.TAG_COMPOUND);
//
//		for (int j = 0; j < nbttaglist.tagCount(); ++j)
//		{
//			NBTTagCompound slot = nbttaglist.getCompoundTagAt(j);
//			int index;
//			if (slot.hasKey("index"))
//			{
//				index = slot.getInteger("index");
//			} else
//			{
//				index = slot.getByte("Slot");
//			}
//			if (index >= 0 && index < contents.length)
//			{
//				setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(slot));
//			}
//		}
//	}
//
//	public void writeToNBT(NBTTagCompound data)
//	{
//		writeToNBT(data, "Items");
//	}
//
//	public void writeToNBT(NBTTagCompound data, String tag)
//	{
//		NBTTagList slots = new NBTTagList();
//		for (byte index = 0; index < contents.length; ++index)
//		{
//			if (contents[index] != null && contents[index].stackSize > 0)
//			{
//				NBTTagCompound slot = new NBTTagCompound();
//				slots.appendTag(slot);
//				slot.setByte("Slot", index);
//				contents[index].writeToNBT(slot);
//			}
//		}
//		data.setTag(tag, slots);
//	}
//
//	public void setTile(IInventoryUpdateable tile)
//	{
//		this.tile = tile;
//	}
//
//	@Override
//	public ItemStack removeStackFromSlot(int slotId)
//	{
//		if (this.contents[slotId] == null)
//		{
//			return null;
//		}
//
//		ItemStack stackToTake = this.contents[slotId];
//		setInventorySlotContents(slotId, null);
//		return stackToTake;
//	}
//
//	public ItemStack[] getStacks()
//	{
//		return contents;
//	}
//
//	@Override
//	public boolean isItemValidForSlot(int i, ItemStack itemstack)
//	{
//		return true;
//	}
//
//	// TODO find out what this is
//	@Override
//	public int getField(int id)
//	{
//		return 0;
//	}
//
//	@Override
//	public void setField(int id, int value)
//	{
//
//	}
//
//	@Override
//	public int getFieldCount()
//	{
//		return 0;
//	}
//
//	@Override
//	public void clear()
//	{
//
//	}
//
//	@Override
//	public String getName()
//	{
//		return name;
//	}
//
//	@Override
//	public boolean hasCustomName()
//	{
//		return false;
//	}
//
//	@Override
//	public ITextComponent getDisplayName()
//	{
//		return new TextComponentString(name);
//	}
}
