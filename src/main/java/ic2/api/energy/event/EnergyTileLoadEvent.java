package ic2.api.energy.event;

import ic2.api.energy.tile.IEnergyTile;

public class EnergyTileLoadEvent extends EnergyTileEvent
{
	public EnergyTileLoadEvent(final IEnergyTile energyTile1)
	{
		super(energyTile1);
	}
}
