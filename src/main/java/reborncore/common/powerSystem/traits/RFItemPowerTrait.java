package reborncore.common.powerSystem.traits;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.jtraits.JTrait;


public class RFItemPowerTrait extends JTrait<IEnergyInterfaceItem> implements IEnergyContainerItem {

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (!PowerSystem.RFPOWENET)
            return 0;
        if (!_self.canAcceptEnergy(container)) {
            return 0;
        }
        maxReceive *= PowerSystem.euPerRF;
        int energyReceived = Math.min(getMaxEnergyStored(container) - getEnergyStored(container), Math.min((int) _self.getMaxTransfer(container) * PowerSystem.euPerRF, maxReceive));

        if (!simulate) {
            _self.setEnergy(_self.getEnergy(container) + energyReceived, container);
        }
        return energyReceived / PowerSystem.euPerRF;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (!PowerSystem.RFPOWENET)
            return 0;
        if (!_self.canAcceptEnergy(container)) {
            return 0;
        }
        maxExtract *= PowerSystem.euPerRF;
        int energyExtracted = Math.min(getEnergyStored(container), Math.min(maxExtract, maxExtract));

        if (!simulate) {
            _self.setEnergy(_self.getEnergy(container) - energyExtracted, container);
        }
        return energyExtracted / PowerSystem.euPerRF;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (!PowerSystem.RFPOWENET)
            return 0;
        return ((int) _self.getEnergy(container) / PowerSystem.euPerRF);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        if (!PowerSystem.RFPOWENET)
            return 0;
        return ((int) _self.getMaxPower(container) / PowerSystem.euPerRF);
    }
}
