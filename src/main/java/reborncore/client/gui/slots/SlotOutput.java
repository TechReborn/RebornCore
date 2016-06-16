package reborncore.client.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotOutput extends BaseSlot
{

	public SlotOutput(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		return false;
	}

	public int getSlotStackLimit()
	{
		return 64;
	}

	@Override
	public boolean canWorldBlockRemove() {
		return true;
	}
}