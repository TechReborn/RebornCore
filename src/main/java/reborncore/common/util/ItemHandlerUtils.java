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

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import reborncore.api.tile.IUpgradeable;

public class ItemHandlerUtils {

	public static void dropContainedItems(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null) {
			return;
		}
		if (tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) != null) {
			IItemHandler inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			dropItemHandler(world, pos, inventory);
		}
		if (tileEntity instanceof IUpgradeable) {
			dropItemHandler(world, pos, ((IUpgradeable) tileEntity).getUpgradeInvetory());
		}
	}

	public static void dropItemHandler(World world, BlockPos pos, IItemHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack itemStack = inventory.getStackInSlot(i);
			if (itemStack.isEmpty()) {
				continue;
			}
			if (itemStack.getCount() > 0) {
				if (itemStack.getItem() instanceof ItemBlock) {
					if (((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockFluidBase
						|| ((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockStaticLiquid
						|| ((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockDynamicLiquid) {
						continue;
					}
				}
			}
			InventoryHelper.spawnItemStack(world, (double) pos.getX(), (double) pos.getY(),
				(double) pos.getZ(), itemStack);
		}
	}
}
