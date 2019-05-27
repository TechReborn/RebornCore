package reborncore.api.power;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import reborncore.common.RebornCoreConfig;

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

	//Checks to ensure that the item has a nbt tag, and upgrades old items to the new format
	private void validateNBT() {
		if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
			stack.getTag().putInt("energy", 0);
		} else {
			if (stack.getTag().contains("charge")) {
				//Upgrades the item from the old format to the new format
				stack.getTag().putInt("energy", stack.getTag().getInt("charge") * RebornCoreConfig.euPerFU);
				stack.getTag().remove("charge");
			}
		}
	}

	public void setEnergyStored(int value) {
		setEnergyInStack(value);
	}

	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (canReceive()) {
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
		if (canExtract()) {
			return 0;
		}
		int energyExtracted = Math.min(getEnergyStored(), maxExtract);
		if (!simulate) {
			setEnergyInStack(getEnergyInStack() - energyExtracted);
		}
		return energyExtracted;
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
}
