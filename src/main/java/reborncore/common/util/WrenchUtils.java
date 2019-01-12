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
import reborncore.common.BaseTileBlock;
import reborncore.common.misc.ModSounds;
import reborncore.common.util.ItemHandlerUtils;


/**
 * @author drcrazy
 *
 */
public class WrenchUtils {

	public static boolean handleWrench(ItemStack stack, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumFacing side) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity == null) {
			return false;
		}

		if (ToolManager.INSTANCE.handleTool(stack, pos, worldIn, playerIn, side, true)) {
			if (playerIn.isSneaking()) {
				if (tileEntity instanceof IToolDrop) {
					ItemStack drop = ((IToolDrop) tileEntity).getToolDrop(playerIn);
					if (drop == null) {
						return false;
					}

					boolean dropContents = true;
					Block block = tileEntity.getBlockState().getBlock();
					if(block instanceof BaseTileBlock){
						ItemStack tileDrop = ((BaseTileBlock) block).getDropWithContents(worldIn, pos, drop).orElse(ItemStack.EMPTY);
						if(!tileDrop.isEmpty()){
							dropContents = false;
							drop = tileDrop;
						}
					}

					if (!worldIn.isRemote) {
						if(dropContents){
							ItemHandlerUtils.dropContainedItems(worldIn, pos);
						}
						if (!drop.isEmpty()) {
							net.minecraft.inventory.InventoryHelper.spawnItemStack(worldIn, (double) pos.getX(),
									(double) pos.getY(), (double) pos.getZ(), drop);
						}
						worldIn.removeTileEntity(pos);
						worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
					}
					worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, ModSounds.BLOCK_DISMANTLE,
							SoundCategory.BLOCKS, 0.6F, 1F);
				}
			} 
			else {
				worldIn.getBlockState(pos).getBlock().rotate(worldIn.getBlockState(pos), worldIn, pos, side);
			}
			return true;
		}
		return false;
	}
}
