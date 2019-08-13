package reborncore.api.power;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ItemPowerManager {
	ItemStack stack;
	IEnergyItemInfo itemPowerInfo;

	public ItemPowerManager(ItemStack stack) {
		this.stack = stack;
		if (stack.getItem() instanceof IEnergyItemInfo) {
			this.itemPowerInfo = (IEnergyItemInfo) stack.getItem();
		}
		validateNBT();
	}

	public ItemPowerManager(ItemStack stack, IEnergyItemInfo itemPowerInfo) {
		this.stack = stack;
		this.itemPowerInfo = itemPowerInfo;
		validateNBT();
	}

	private int getEnergyInStack() {
		validateNBT();
		return stack.getTag().getInt("energy");
	}

	private void setEnergyInStack(int energy) {
		validateNBT();
		stack.getTag().putInt("energy", energy);
	}

	//Checks to ensure that the item has a nbt tag
	private void validateNBT() {
		if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
			stack.getTag().putInt("energy", 0);
		} 
	}

	public void setEnergyStored(int value) {
		setEnergyInStack(value);
	}

	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive()) {
			return 0;
		}
		int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(),
		                              Math.min((int) itemPowerInfo.getMaxInput(), maxReceive));

		if (!simulate) {
			setEnergyInStack(getEnergyInStack() + energyReceived);
		}
		return energyReceived;
	}

	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}
		int energyExtracted = Math.min(getEnergyStored(), maxExtract);
		if (!simulate) {
			setEnergyInStack(getEnergyInStack() - energyExtracted);
		}
		return energyExtracted;
	}
	
	public int useEnergy(int maxUse, boolean simulate) {
		int energyUsed = Math.min(getEnergyStored(), maxUse);
		if (!simulate) {
			setEnergyInStack(getEnergyInStack() - energyUsed);
		}
		return energyUsed;
	}


	public int getEnergyStored() {
		return getEnergyInStack();
	}


	public int getMaxEnergyStored() {
		return (int) itemPowerInfo.getCapacity();
	}


	public boolean canExtract() {
		return itemPowerInfo.getMaxOutput() != 0;
	}

	public boolean canReceive() {
		return itemPowerInfo.getMaxInput() != 0;
	}

	public void setEnergy(int energy) {
		setEnergyInStack(energy);
	}
	
	public ItemStack getStack() {
		return stack;
	}
}
