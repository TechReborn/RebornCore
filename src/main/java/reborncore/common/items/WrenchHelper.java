/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package reborncore.common.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import reborncore.api.IToolDrop;
import reborncore.api.ToolManager;
import reborncore.api.tile.IWrenchable;
import reborncore.common.misc.ModSounds;
import reborncore.common.util.InventoryHelper;


/**
 * @author drcrazy
 *
 */
public class WrenchHelper {

	public static boolean handleWrench(ItemStack stack, World world, BlockPos pos, EntityPlayer playerIn, EnumFacing side) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null) {
			return false;
		}

		if (ToolManager.INSTANCE.handleTool(stack, pos, world, playerIn, side, true)) {
			if (playerIn.isSneaking()) {
				if (tileEntity instanceof IToolDrop) {
					ItemStack drop = ((IToolDrop) tileEntity).getToolDrop(playerIn);
					if (drop == null) {
						return false;
					}
					if (!world.isRemote) {
						InventoryHelper.dropInventoryItems(world, pos);
						if (!drop.isEmpty()) {
							net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), drop);
						}
						world.removeTileEntity(pos);
						world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
					}
					world.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, ModSounds.BLOCK_DISMANTLE,
							SoundCategory.BLOCKS, 0.6F, 1F);
				}
			} 
			else {
				Block block = world.getBlockState(pos).getBlock();
				if (block instanceof IWrenchable)
					((IWrenchable) block).setFacing(world, pos, side, playerIn);
				else
					block.rotateBlock(world, pos, side);
			}
			return true;
		}
		return false;
	}
}
