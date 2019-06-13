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

package reborncore.common.util;

import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.api.tile.IUpgradeable;

public class ItemHandlerUtils {

	public static void dropContainedItems(World world, BlockPos pos) {
		BlockEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity == null) {
			return;
		}
		if (tileEntity instanceof Inventory) {
			Inventory inventory = (Inventory) tileEntity;
			dropItemHandler(world, pos, inventory);
		}
		if (tileEntity instanceof IUpgradeable) {
			dropItemHandler(world, pos, ((IUpgradeable) tileEntity).getUpgradeInvetory());
		}
	}

	public static void dropItemHandler(World world, BlockPos pos, Inventory inventory) {
		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (itemStack.isEmpty()) {
				continue;
			}
			if (itemStack.getCount() > 0) {
				if (itemStack.getItem() instanceof BlockItem) {
					if (((BlockItem) itemStack.getItem()).getBlock() instanceof FluidBlock) {
						continue;
					}
				}
			}
			ItemScatterer.spawn(world, (double) pos.getX(), (double) pos.getY(),
				(double) pos.getZ(), itemStack);
		}
	}
}
