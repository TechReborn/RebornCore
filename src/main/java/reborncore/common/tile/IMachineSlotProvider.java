package reborncore.common.tile;

import net.minecraft.inventory.IInventory;

/**
 * Created by Mark on 12/04/2017.
 */
public interface IMachineSlotProvider {

	int[] getInputSlots();

	int[] getOuputSlots();

	IInventory getMachineInv();

}
