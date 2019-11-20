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


package reborncore.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class TorchHelper {
	public static EnumActionResult placeTorch(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
	                                          EnumFacing side, float xOffset, float yOffset, float zOffset, EnumHand hand) {
		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			ItemStack torchStack = player.inventory.getStackInSlot(i);
			if (torchStack.isEmpty() || !torchStack.getUnlocalizedName().toLowerCase().contains("torch"))
				continue;
			Item item = torchStack.getItem();
			if (!(item instanceof ItemBlock))
				continue;
			int oldMeta = torchStack.getItemDamage();
			int oldSize = torchStack.getCount();
			EnumActionResult result = torchStack.onItemUse(player, world, pos, hand, side, xOffset, yOffset, zOffset);
			if (player.capabilities.isCreativeMode) {
				torchStack.setItemDamage(oldMeta);
				torchStack.setCount(oldSize);
			} else if (torchStack.getCount() <= 0) {
				ForgeEventFactory.onPlayerDestroyItem(player, torchStack, hand);
				player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
			}
			if (result == EnumActionResult.SUCCESS) {
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}
}
