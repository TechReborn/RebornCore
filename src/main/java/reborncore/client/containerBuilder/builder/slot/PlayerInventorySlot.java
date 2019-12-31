package reborncore.client.containerBuilder.builder.slot;

import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;

/**
 * Created by drcrazy on 31-Dec-19 for TechReborn-1.15.
 */
public class PlayerInventorySlot extends Slot {

    public boolean doDraw;

    public PlayerInventorySlot(Inventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
        this.doDraw = true;
    }

    @Override
    public boolean doDrawHoveringEffect() {
        return doDraw? true : false;
    }
}
