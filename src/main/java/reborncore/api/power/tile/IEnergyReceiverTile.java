package reborncore.api.power.tile;

import net.minecraft.util.EnumFacing;
import reborncore.api.power.IEnergyInterfaceTile;

public interface IEnergyReceiverTile extends IEnergyInterfaceTile {

    /**
     * @param direction The direction to insert energy into
     * @return if the tile can accept energy from the direction
     */
    default boolean canAcceptEnergy(EnumFacing direction) {
        return true;
    }

    /**
     * Return -1 if you don't want to accept power ever.
     *
     * @return The max amount of energy that can be added to the tile in one tick.
     */
    default double getMaxInput() {
        return getTier().voltage;
    }

}
