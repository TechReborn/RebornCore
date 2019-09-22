package reborncore.api.power;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import team.reborn.energy.*;

public final class ItemPowerHolder {

	private static boolean setup = false;

	public static void setup(){
		if(setup){
			return;
		}
		setup = true;
		Energy.registerHolder(object -> {
			if(object instanceof ItemStack){
				return !((ItemStack) object).isEmpty() && ((ItemStack) object).getItem() instanceof EnergyHolder;
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

}
