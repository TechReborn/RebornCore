package reborncore.common.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import reborncore.common.container.RebornContainer;

import javax.annotation.Nonnull;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class LogicContainer extends RebornContainer {
	@Nonnull
	LogicController logicController;
	@Nonnull
	EntityPlayer player;

	public LogicContainer(EntityPlayer player, LogicController logicController) {
		super();
		this.player = player;
		this.logicController = logicController;
		if (logicController.getSlots() != null) {
			for (Slot s : logicController.getSlots()) {
				addSlotToContainer(s);
			}
		}
		drawPlayersInv(player, logicController.inventoryOffsetX(), logicController.inventoryOffsetY());
		drawPlayersHotBar(player, logicController.inventoryOffsetX(), logicController.inventoryOffsetY() + 58);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
