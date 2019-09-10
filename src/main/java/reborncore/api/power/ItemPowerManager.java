package reborncore.api.power;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import team.reborn.energy.*;

public class ItemPowerManager {

	static {
		Energy.registerHolder(object -> {
			if(object instanceof ItemStack){
				return ((ItemStack) object).getItem() instanceof EnergyHolder;
			}
			return false;
		}, object -> {
			final ItemStack stack = (ItemStack) object;
			final EnergyHolder energyHolder = (EnergyHolder) stack.getItem();
			return new EnergyStorage() {
				@Override
				public double getStored(EnergySide face) {
					validateNBT();
					return stack.getTag().getDouble("energy");
				}

				@Override
				public void setStored(double amount) {
					validateNBT();
					stack.getTag().putDouble("energy", amount);
				}

				@Override
				public double getMaxStoredPower() {
					return energyHolder.getMaxStoredPower();
				}

				@Override
				public EnergyTier getTier() {
					return energyHolder.getTier();
				}

				private void validateNBT() {
					if (!stack.hasTag()) {
						stack.setTag(new CompoundTag());
						stack.getTag().putInt("energy", 0);
					}
				}
			};
		});
	}

	private final ItemStack stack;
	private final double maxOutput;
	private final double maxInput;
	private final double maxStored;

	public ItemPowerManager(ItemStack stack) {
		this.stack = stack;

		maxOutput = Energy.of(stack).getMaxOutput();
		maxInput = Energy.of(stack).getMaxInput();
		maxStored = Energy.of(stack).getEnergy();
	}

	private double getEnergyInStack() {
		return Energy.of(stack).getEnergy();
	}

	private void setEnergyInStack(double energy) {
		Energy.of(stack).set(energy);
	}
	

	public void setEnergyStored(double value) {
		setEnergyInStack(value);
	}

	public double receiveEnergy(double maxReceive, boolean simulate) {
		if (!canReceive()) {
			return 0;
		}
		double energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(),
		                              Math.min((double) Energy.of(stack).getMaxInput(), maxReceive));

		if (!simulate) {
			setEnergyInStack(getEnergyInStack() + energyReceived);
		}
		return energyReceived;
	}

	public double extractEnergy(double maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}
		double energyExtracted = Math.min(getEnergyStored(), maxExtract);
		if (!simulate) {
			setEnergyInStack(getEnergyInStack() - energyExtracted);
		}
		return energyExtracted;
	}
	
	public double useEnergy(double maxUse, boolean simulate) {
		double energyUsed = Math.min(getEnergyStored(), maxUse);
		if (!simulate) {
			setEnergyInStack(getEnergyInStack() - energyUsed);
		}
		return energyUsed;
	}


	public double getEnergyStored() {
		return getEnergyInStack();
	}


	public double getMaxEnergyStored() {
		return maxStored;
	}


	public boolean canExtract() {
		return maxOutput != 0;
	}

	public boolean canReceive() {
		return maxInput != 0;
	}

	public void setEnergy(double energy) {
		setEnergyInStack(energy);
	}
	
	public ItemStack getStack() {
		return stack;
	}
}
