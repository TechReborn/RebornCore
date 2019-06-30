package reborncore.api.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.DefaultedList;

//This is a temp class to get around the pain of moving back to vanilla's item handler
public class InventoryWrapper implements Inventory {

	public int size;
	private DefaultedList<ItemStack> stacks;

	public InventoryWrapper(int size) {
		this.size = size;
		stacks = DefaultedList.create(size, ItemStack.EMPTY);
	}

	@Deprecated
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack stackInSlot = getInvStack(slot);

		int size;
		if (!stackInSlot.isEmpty()) {
			if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxCount(), getInvMaxStackAmount())) {
				return stack;
			}

			if (!areStackable(stack, stackInSlot)) {
				return stack;
			}

			if (!isValidInvStack(slot, stack)) {
				return stack;
			}

			size = Math.min(stack.getMaxCount(), getInvMaxStackAmount()) - stackInSlot.getCount();

			if (stack.getCount() <= size) {
				if (!simulate) {
					ItemStack copy = stack.copy();
					copy.increment(stackInSlot.getCount());
					setInvStack(slot, copy);
					markDirty();
				}

				return ItemStack.EMPTY;
			} else {
				stack = stack.copy();
				if (!simulate) {
					ItemStack copy = stack.split(size);
					copy.increment(stackInSlot.getCount());
					setInvStack(slot, copy);
					markDirty();
					return stack;
				} else {
					stack.decrement(size);
					return stack;
				}
			}
		} else {
			if (isValidInvStack(slot, stack))
				return stack;

			size = Math.min(stack.getMaxCount(), getInvMaxStackAmount());
			if (size < stack.getCount()) {
				stack = stack.copy();
				if (!simulate) {
					setInvStack(slot, stack.split(size));
					markDirty();
					return stack;
				} else {
					stack.decrement(size);
					return stack;
				}
			} else {
				if (!simulate) {
					setInvStack(slot, stack);
					markDirty();
				}
				return ItemStack.EMPTY;
			}
		}

	}

	@Deprecated
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = getInvStack(slot);
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		if (simulate) {
			if (stack.getCount() < amount) {
				return stack.copy();
			} else {
				ItemStack newStack = stack.copy();
				newStack.setCount(amount);
				return newStack;
			}
		} else {
			int size = Math.min(stack.getCount(), amount);
			ItemStack newStack = getStack(slot);
			newStack.decrement(size);
			markDirty();
			return newStack;
		}
	}

	private static boolean areStackable(ItemStack isOne, ItemStack isTwo) {
		if (isOne.isEmpty() || !isOne.isItemEqual(isTwo) || isOne.hasTag() != isTwo.hasTag()) {
			return false;
		}
		return (!isOne.hasTag() || isOne.getTag().equals(isTwo.getTag()));
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

	public ItemStack getStack(int slot) {
		return getInvStack(slot);
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		setInvStack(slot, stack);
	}

	public DefaultedList<ItemStack> getStacks() {
		return stacks;
	}
}
