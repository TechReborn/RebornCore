package reborncore.common.powerSystem.forge;

import net.minecraftforge.energy.IEnergyStorage;

//This is a simple interface to get around the input/tick limit on our items. This may not be part of the FE spec so we might be able to remove this and that limit
public interface IEnergyStorageModifiable extends IEnergyStorage {

	void setEnergy(int energy);
}
