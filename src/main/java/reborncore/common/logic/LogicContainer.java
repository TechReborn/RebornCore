/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
