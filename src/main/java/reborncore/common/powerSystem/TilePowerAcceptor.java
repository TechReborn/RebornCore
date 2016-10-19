package reborncore.common.powerSystem;

import reborncore.api.power.EnumPowerTier;

/**
 * Created by Mark on 19/10/2016.
 */
@Deprecated
public abstract class TilePowerAcceptor extends TileEnergyBase {
    public TilePowerAcceptor(EnumPowerTier tier, int capacity) {
        super(tier, capacity);
    }
}
