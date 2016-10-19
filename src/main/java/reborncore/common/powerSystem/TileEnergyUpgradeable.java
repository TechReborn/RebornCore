package reborncore.common.powerSystem;

import net.minecraft.nbt.NBTTagCompound;
import reborncore.api.power.EnumPowerTier;
import reborncore.common.util.inventory.Inventory;

/**
 * Created by Lordmau5 on 12.06.2016.
 */
public abstract class TileEnergyUpgradeable extends TileEnergyBase {

    /* Inventory Setup */
    private final Inventory inventoryUpgrades;
    /*-----------------*/

    public TileEnergyUpgradeable(EnumPowerTier tier, int capacity) {
        super(tier, capacity);

        this.inventoryUpgrades = new Inventory("Upgrades", 4, 64, this);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.inventoryUpgrades.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        this.inventoryUpgrades.writeToNBT(compound);

        return super.writeToNBT(compound);
    }

    public Inventory getInventoryUpgrades() {
        return getInventoryUpgrades();
    }
}
