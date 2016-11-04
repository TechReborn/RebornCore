package reborncore.client.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created by modmuss50 on 11/04/2016.
 */
@Deprecated //use the correct package one
public class BaseSlot extends reborncore.client.gui.slots.BaseSlot {
    public BaseSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    public boolean canWorldBlockRemove(){
        return true;
    }

}
