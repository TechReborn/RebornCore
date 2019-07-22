/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
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

package reborncore.client.containerBuilder.builder.slot;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import reborncore.api.blockentity.IUpgrade;
import reborncore.api.blockentity.IUpgradeable;
import reborncore.client.containerBuilder.IRightClickHandler;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.common.util.RebornInventory;

public class UpgradeSlot extends BaseSlot implements IRightClickHandler {

	public UpgradeSlot(final net.minecraft.inventory.Inventory inventory, final int index, final int xPosition, final int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	@Override
	public boolean canInsert(final ItemStack stack) {
		if(!(stack.getItem() instanceof IUpgrade)){
			return false;
		}
		IUpgrade upgrade = (IUpgrade) stack.getItem();
		IUpgradeable upgradeable = null;
		RebornInventory inv = (RebornInventory) inventory;
		BlockEntity blockEntity = inv.getBlockEntity();
		if (blockEntity instanceof IUpgradeable) {
			upgradeable = (IUpgradeable) blockEntity;
		}
		return upgrade.isValidForInventory(upgradeable, stack) && (upgradeable == null || upgradeable.isUpgradeValid(upgrade, stack));
	}

	@Override
	public int getMaxStackAmount() {
		return 1;
	}

	@Override
	public boolean handleRightClick(int slotID, PlayerEntity player, BuiltContainer container) {
		if (inventory instanceof RebornInventory) {
			RebornInventory inv = (RebornInventory) inventory;
			BlockEntity blockEntity = inv.getBlockEntity();
			if (blockEntity instanceof IUpgradeable) {
				IUpgradeable upgradeable = (IUpgradeable) blockEntity;
				if (upgradeable.canBeUpgraded()) {
					ItemStack stack = upgradeable.getUpgradeInvetory().getInvStack(slotID);
					if (!stack.isEmpty() && stack.getItem() instanceof IRightClickHandler) {
						if (player.world.isClient) {
							((IRightClickHandler) stack.getItem()).handleRightClick(slotID, player, container);
						}
					}
				}
			}
		}

		return true;
	}
}
