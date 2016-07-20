package reborncore.api.power.tile;

import net.minecraft.util.EnumFacing;
import reborncore.api.power.IEnergyInterfaceTile;

public interface IEnergyProducerTile extends IEnergyInterfaceTile {

    /**
     * @param direction The direction to provide energy from
     * @return true if the tile can provide energy to that direction
     */
    default boolean canProvideEnergy(EnumFacing direction) {
        return true;
    }

    /**
     * Gets the max output, set to -1 if you don't want the tile to provide
     * energy
     *
     * @return the max amount of energy outputted per tick.
     * @deprecated use {@link IEnergyProducerTile}
     */
    default double getMaxOutput() {
        return getTier().voltage;
    }

}
