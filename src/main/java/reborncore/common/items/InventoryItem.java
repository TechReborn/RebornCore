package reborncore.common.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InventoryItem implements IItemHandler, IItemHandlerModifiable, ICapabilityProvider {

	@Nonnull ItemStack stack;
	int size;

	private InventoryItem(@Nonnull ItemStack stack, int size) {
		Validate.notNull(stack);
		Validate.isTrue(!stack.isEmpty());
		this.size = size;
		this.stack = stack;
	}

	public static InventoryItem getItemInvetory(ItemStack stack, int size) {
		return new InventoryItem(stack, size);
	}

	public ItemStack getStack() {
		return stack;
	}

	public NBTTagCompound getInvData() {
		Validate.isTrue(!stack.isEmpty());
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		if (!stack.getTagCompound().hasKey("inventory")) {
			stack.getTagCompound().setTag("inventory", new NBTTagCompound());
		}
		return stack.getTagCompound().getCompoundTag("inventory");
	}

	public NBTTagCompound getSlotData(int slot) {
		validateSlotIndex(slot);
		NBTTagCompound invData = getInvData();
		if (!invData.hasKey("slot_" + slot)) {
			invData.setTag("slot_" + slot, new NBTTagCompound());
		}
		return invData.getCompoundTag("slot_" + slot);
	}

	public void setSlotData(int slot, NBTTagCompound tagCompound) {
		validateSlotIndex(slot);
		Validate.notNull(tagCompound);
		NBTTagCompound invData = getInvData();
		invData.setTag("slot_" + slot, tagCompound);
	}

	public List<ItemStack> getAllStacks() {
		return IntStream.range(0, size)
			.mapToObj(this::getStackInSlot)
			.collect(Collectors.toList());
	}

	@Override
	public int getSlots() {
		return size;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return new ItemStack(getSlotData(slot));
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		setSlotData(slot, stack.writeToNBT(new NBTTagCompound()));
	}

	//insertItem and extractItem are the forge methods just adjusted to work with items
	@Nonnull
	@Override
	public ItemStack insertItem(int slot,
	                            @Nonnull
		                            ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		validateSlotIndex(slot);
		ItemStack existing = getStackInSlot(slot);
		int limit = getStackLimit(slot, stack);
		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
				return stack;
			}
			limit -= existing.getCount();
		}
		if (limit <= 0) {
			return stack;
		}
		boolean reachedLimit = stack.getCount() > limit;
		if (!simulate) {
			if (existing.isEmpty()) {
				setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
		}
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}
		validateSlotIndex(slot);
		ItemStack existing = getStackInSlot(slot);

		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int toExtract = Math.min(amount, existing.getMaxStackSize());
		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				setStackInSlot(slot, ItemStack.EMPTY);
			}
			return existing;
		} else {
			if (!simulate) {
				setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
			}
			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	public void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= size) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + size + ")");
		}

	}

	public int getStackLimit(int slot,
	                         @Nonnull
		                         ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public boolean hasCapability(
		@Nonnull
			Capability<?> capability,
		@Nullable
			EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(
		@Nonnull
			Capability<T> capability,
		@Nullable
			EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
		}
		return null;
	}
}
