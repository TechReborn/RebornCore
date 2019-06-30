package reborncore.api.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.DefaultedList;

public abstract class InventoryBase implements Inventory {

	private int size;
	private DefaultedList<ItemStack> stacks;

	public InventoryBase(int size) {
		this.size = size;
		stacks = DefaultedList.create(size, ItemStack.EMPTY);
	}

	public Tag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		Inventories.toTag(tag, stacks);
		return tag;
	}

	public void deserializeNBT(CompoundTag tag) {
		stacks = DefaultedList.create(size, ItemStack.EMPTY);
		Inventories.fromTag(tag, stacks);
	}

	@Override
	public int getInvSize() {
		return size;
	}

	@Override
	public boolean isInvEmpty() {
		return stacks.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getInvStack(int i) {
		return stacks.get(i);
	}

	@Override
	public ItemStack takeInvStack(int i, int i1) {
		ItemStack stack = Inventories.splitStack(stacks, i, i1);
		if (!stack.isEmpty()) {
			this.markDirty();
		}
		return stack;
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return Inventories.removeStack(stacks, i);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		stacks.set(i, itemStack);
		if (itemStack.getCount() > this.getInvMaxStackAmount()) {
			itemStack.setCount(this.getInvMaxStackAmount());
		}

		this.markDirty();
	}

	@Override
	public void markDirty() {
		//Stuff happens in the super methods
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public void clear() {
		stacks.clear();
	}

	public DefaultedList<ItemStack> getStacks() {
		return stacks;
	}
}
