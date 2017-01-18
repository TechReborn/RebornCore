package reborncore.common.powerSystem.mixin;

import net.minecraft.item.ItemStack;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.common.RebornCoreConfig;
import cofh.api.energy.IEnergyContainerItem;
import reborncore.mixin.api.Inject;
import reborncore.mixin.api.Mixin;

@Mixin(target = "")
public class RFItemPowerMixin  implements IEnergyContainerItem
{

	@Inject
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate)
	{
		if (!RebornCoreConfig.getRebornPower().rf())
			return 0;
		if (!getRFInterface().canAcceptEnergy(container))
		{
			return 0;
		}
		maxReceive *= RebornCoreConfig.euPerRF;
		int energyReceived = Math.min(getMaxEnergyStored(container) - getEnergyStored(container),
				Math.min((int) getRFInterface().getMaxTransfer(container) * RebornCoreConfig.euPerRF, maxReceive));

		if (!simulate)
		{
			getRFInterface().setEnergy(getRFInterface().getEnergy(container) + energyReceived, container);
		}
		return energyReceived / RebornCoreConfig.euPerRF;
	}

	@Inject
	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
	{
		if (!RebornCoreConfig.getRebornPower().rf())
			return 0;
		if (!getRFInterface().canAcceptEnergy(container))
		{
			return 0;
		}
		maxExtract *= RebornCoreConfig.euPerRF;
		int energyExtracted = Math.min(getEnergyStored(container), Math.min(maxExtract, maxExtract));

		if (!simulate)
		{
			getRFInterface().setEnergy(getRFInterface().getEnergy(container) - energyExtracted, container);
		}
		return energyExtracted / RebornCoreConfig.euPerRF;
	}

	@Inject
	@Override
	public int getEnergyStored(ItemStack container)
	{
		if (!RebornCoreConfig.getRebornPower().rf())
			return 0;
		return ((int) getRFInterface().getEnergy(container) / RebornCoreConfig.euPerRF);
	}

	@Inject
	@Override
	public int getMaxEnergyStored(ItemStack container)
	{
		if (!RebornCoreConfig.getRebornPower().rf())
			return 0;
		return ((int) getRFInterface().getMaxPower(container) / RebornCoreConfig.euPerRF);
	}

	@Inject
	public IEnergyInterfaceItem getRFInterface(){
		return (IEnergyInterfaceItem)this;
	}
}
