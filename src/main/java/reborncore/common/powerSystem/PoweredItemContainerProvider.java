package reborncore.common.powerSystem;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;
import reborncore.common.powerSystem.tesla.AdvancedTeslaItemContainer;
import reborncore.common.powerSystem.tesla.TeslaManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by modmuss50 on 18/01/2017.
 */
public class PoweredItemContainerProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {

	ItemStack stack;

	public PoweredItemContainerProvider(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public boolean hasCapability(
		@Nonnull
			Capability<?> capability,
		@Nullable
			EnumFacing facing) {
		if (TeslaManager.isTeslaEnabled(RebornCoreConfig.getRebornPower()) && TeslaManager.manager.isTeslaCapability(capability)) {
			return true;
		}
		if (RebornCoreConfig.getRebornPower().forge() && capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public <T> T getCapability(
		@Nonnull
			Capability<T> capability,
		@Nullable
			EnumFacing facing) {
		if (TeslaManager.isTeslaEnabled(RebornCoreConfig.getRebornPower()) && TeslaManager.manager.isTeslaCapability(capability)) {
			return (T) new AdvancedTeslaItemContainer(stack);
		}
		if (RebornCoreConfig.getRebornPower().forge() && capability == CapabilityEnergy.ENERGY) {
			return (T) new ForgePowerItemManager(stack);
		}
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {

	}
}
