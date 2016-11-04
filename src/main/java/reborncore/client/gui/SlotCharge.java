package reborncore.client.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import reborncore.api.power.IEnergyItemInfo;

/**
 * Created by Rushmead
 */
@Deprecated //use the correct package one
public class SlotCharge extends reborncore.client.gui.slots.SlotCharge
{
	public SlotCharge(IInventory inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override public boolean isItemValid(ItemStack stack)
	{
		if(stack.getItem() instanceof IEnergyItemInfo){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean canWorldBlockRemove() {
		return false;
	}
}
