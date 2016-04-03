package reborncore.common.powerSystem.traits;

import net.minecraft.item.ItemStack;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.jtraits.JTrait;
import cofh.api.energy.IEnergyContainerItem;

public class RFItemPowerTrait extends JTrait<IEnergyInterfaceItem> implements IEnergyContainerItem
{

	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate)
	{
		if (!RebornCoreConfig.getRebornPower().rf())
			return 0;
		if (!_self.canAcceptEnergy(container))
		{
			return 0;
		}
		maxReceive *= RebornCoreConfig.euPerRF;
		int energyReceived = Math.min(getMaxEnergyStored(container) - getEnergyStored(container),
				Math.min((int) _self.getMaxTransfer(container) * RebornCoreConfig.euPerRF, maxReceive));

		if (!simulate)
		{
			_self.setEnergy(_self.getEnergy(container) + energyReceived, container);
		}
		return energyReceived / RebornCoreConfig.euPerRF;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
	{
		if (!RebornCoreConfig.getRebornPower().rf())
			return 0;
		if (!_self.canAcceptEnergy(container))
		{
			return 0;
		}
		maxExtract *= RebornCoreConfig.euPerRF;
		int energyExtracted = Math.min(getEnergyStored(container), Math.min(maxExtract, maxExtract));

		if (!simulate)
		{
			_self.setEnergy(_self.getEnergy(container) - energyExtracted, container);
		}
		return energyExtracted / RebornCoreConfig.euPerRF;
	}

	@Override
	public int getEnergyStored(ItemStack container)
	{
		if (!RebornCoreConfig.getRebornPower().rf())
			return 0;
		return ((int) _self.getEnergy(container) / RebornCoreConfig.euPerRF);
	}

	@Override
	public int getMaxEnergyStored(ItemStack container)
	{
		if (!RebornCoreConfig.getRebornPower().rf())
			return 0;
		return ((int) _self.getMaxPower(container) / RebornCoreConfig.euPerRF);
	}
}
