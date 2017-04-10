package reborncore.api.tile;

import net.minecraft.inventory.IInventory;

public interface IUpgradeable {

	public default boolean canBeUpgraded() {
		return false;
	}

	public IInventory getUpgradeInvetory();

	public int getUpgradeSlotCount();

}
