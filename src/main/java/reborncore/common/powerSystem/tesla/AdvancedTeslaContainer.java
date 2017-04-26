package reborncore.common.powerSystem.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.TilePowerAcceptor;

/**
 * Created by modmuss50 on 06/05/2016.
 */
public class AdvancedTeslaContainer implements ITeslaConsumer, ITeslaHolder, ITeslaProducer {

	public TilePowerAcceptor tile;

	public AdvancedTeslaContainer(TilePowerAcceptor tile) {
		this.tile = tile;
	}

	public AdvancedTeslaContainer(NBTBase nbt, TilePowerAcceptor tile) {
		this.tile = tile;
		this.readNBT(nbt);
	}

	public long getStoredPower() {
		return (long) tile.getEnergy() * RebornCoreConfig.euPerFU;
	}

	//Receive
	public long givePower(long tesla, boolean simulated) {
		double euToAdd = tesla * RebornCoreConfig.euPerFU;

		if (tile.canAddEnergy(euToAdd)) {
			double euAdded = tile.addEnergy(euToAdd);
			return (long) (euAdded / RebornCoreConfig.euPerFU);
		} else {
			return 0;
		}
	}

	//Take power out
	public long takePower(long tesla, boolean simulated) {
		double euToTake = tesla * RebornCoreConfig.euPerFU;

		if (tile.canUseEnergy(euToTake)) {
			double euTaken = tile.useEnergy(euToTake);
			return (long) (euTaken / RebornCoreConfig.euPerFU);
		} else {
			return 0;
		}
	}

	public long getCapacity() {
		return (long) tile.getMaxPower() * RebornCoreConfig.euPerFU;
	}

	public long getInputRate() {
		return (long) tile.getMaxInput() * RebornCoreConfig.euPerFU;
	}

	public long getOutputRate() {
		return (long) tile.getMaxOutput() * RebornCoreConfig.euPerFU;
	}

	public NBTBase writeNBT() {
		NBTTagCompound dataTag = new NBTTagCompound();
		return dataTag;
	}

	public void readNBT(NBTBase nbt) {
	}

	public boolean isInputSide() {
		return true;
	}

	public boolean isOutputSide() {
		return true;
	}

}
