package reborncore.api.power;

import net.minecraft.util.EnumFacing;
import reborncore.api.power.tile.IEnergyProducerTile;
import reborncore.api.power.tile.IEnergyReceiverTile;

public interface IEnergyInterfaceTile
{

	/**
	 * @return Amount of energy in the tile
	 */
	double getEnergy();

	/**
	 * Sets the energy in the tile
	 *
	 * @param energy
	 *            the amount of energy to set.
	 */
	void setEnergy(double energy);

	/**
	 * Gets the max stored energy in the tile
	 *
	 * @return The max energy
	 */
	double getMaxPower();

	/**
	 *
	 * Gets the teir, used for machine explosions
	 *
	 * @return the teir
	 */
	EnumPowerTier getTier();

	/**
	 * @param energy
	 *            amount of energy to add to the tile
	 * @return will return true if can fit all
	 */
	default boolean canAddEnergy(double energy) {
		return getMaxPower() - getEnergy() >= energy;
	}

	/**
	 * Will try add add the full amount of energy.
	 *
	 * @param energy
	 *            amount to add
	 * @return The amount of energy that was added.
	 */
	default double addEnergy(double energy) {
		return addEnergy(energy, false);
	}

	/**
	 * Will try add add the full amount of energy, if simulate is true it wont
	 * add the energy
	 *
	 * @param energy
	 *            amount to add
	 * @param simulate
	 *            set to true to simulate not perform the action.
	 * @return The amount of energy that was added.
	 */
	default double addEnergy(double energy, boolean simulate) {
		double canStore = getMaxPower() - getEnergy();
		if(canStore >= energy) {
			if(!simulate) setEnergy(getEnergy() + energy);
			return energy;
		} else {
			if(!simulate) setEnergy(getEnergy() + canStore);
			return canStore;
		}
	}

	/**
	 * Returns true if it can use the full amount of energy
	 *
	 * @param energy
	 *            amount of energy to use from the tile.
	 * @return if all the energy can be used.
	 */
	default boolean canUseEnergy(double energy) {
		return getEnergy() >= energy;
	}

	/**
	 * Will try and use the full amount of energy
	 *
	 * @param energy
	 *            energy to use
	 * @return the amount of energy used
	 */
	default double useEnergy(double energy) {
		return useEnergy(energy, false);
	}

	/**
	 * Will try and use the full amount of energy, if simulate is true it wont
	 * add the energy
	 *
	 * @param energy   energy to use
	 * @param simulate set to true to simulate not perform the action.
	 * @return the amount of energy used
	 */
	default double useEnergy(double energy, boolean simulate) {
		double canUse = getEnergy();
		if(canUse >= energy) {
			if(!simulate) setEnergy(canUse - energy);
			return energy;
		} else {
			if(!simulate) setEnergy(0);
			return canUse;
		}
	}

	/**
	 * @param direction The direction to insert energy into
	 * @return if the tile can accept energy from the direction
	 * @deprecated use {@link IEnergyReceiverTile}
	 */
	@Deprecated default boolean canAcceptEnergy(EnumFacing direction) {
		return false;
	}

	/**
	 * Return -1 if you don't want to accept power ever.
	 *
	 * @return The max amount of energy that can be added to the tile in one tick.
	 * @deprecated use {@link IEnergyReceiverTile}
	 */
	@Deprecated default double getMaxInput() {
		return -1;
	}

	/**
	 * @param direction The direction to provide energy from
	 * @return true if the tile can provide energy to that direction
	 * @deprecated use {@link IEnergyProducerTile}
	 */
	@Deprecated default boolean canProvideEnergy(EnumFacing direction) {
		return false;
	}

	/**
	 * Gets the max output, set to -1 if you don't want the tile to provide
	 * energy
	 *
	 * @return the max amount of energy outputted per tick.
	 * @deprecated use {@link IEnergyProducerTile}
	 */
	@Deprecated default double getMaxOutput() {
		return -1;
	}

}
