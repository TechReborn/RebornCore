package reborncore.client.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created by modmuss50 on 11/04/2016.
 */
public class BaseSlot extends Slot {
	public BaseSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	public boolean canWorldBlockRemove() {
		return true;
	}

}
