package reborncore.client.gui;

import cofh.api.energy.IEnergyContainerItem;
import ic2.api.item.IElectricItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import reborncore.api.power.IEnergyInterfaceItem;
import reborncore.api.power.IEnergyItemInfo;

/**
 * Created by Rushmead
 */
public class SlotCharge extends BaseSlot {
    public SlotCharge(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof IEnergyItemInfo ||
                item instanceof IEnergyInterfaceItem ||
                item instanceof IEnergyContainerItem ||
                item instanceof IElectricItem;
    }

    @Override
    public boolean canWorldBlockRemove() {
        return true;
    }

}
