package reborncore.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import reborncore.common.tile.TileMachineBase;

public interface IInventoryAccess<T extends TileMachineBase> {

	public boolean canHandleIO(int slotID, ItemStack stack, EnumFacing face, AccessDirection direction, T tile);

	public enum AccessDirection{
		INSERT,
		EXTRACT
	}

}
