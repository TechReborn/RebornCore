package api.energy.event;

import api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.*;

public class EnergyTileLoadEvent extends EnergyTileEvent
{
    public EnergyTileLoadEvent(final IEnergyTile energyTile1) {
        super(energyTile1);
    }
}
