package reborncore.api.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;

//This is a temp class to get around the pain of moving back to vanilla's item handler
public class InventoryWrapper implements Inventory {

	public ItemStack[] stacks;

	public ItemStack getStack(int slot){
		return getInvStack(slot);
	}

	public void setStackInSlot(int slot, ItemStack stack) {

	}

	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return null;
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return null;
	}

	public Tag serializeNBT() {
		return null;
	}

	public void deserializeNBT(Tag tag){

	}

	//TODO make this do something

	@Override
	public int getInvSize() {
		return 0;
	}

	@Override
	public boolean isInvEmpty() {
		return false;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return null;
	}

	@Override
	public ItemStack takeInvStack(int i, int i1) {
		return null;
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return null;
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {

	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public void clear() {

	}


}
