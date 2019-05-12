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

package reborncore.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import reborncore.api.ToolManager;
import reborncore.common.RebornCoreConfig;

import java.util.ArrayList;
import java.util.List;

public class BlockWrenchEventHandler {

	public static List<Block> wrenableBlocks = new ArrayList<>();

	@SubscribeEvent
	public static void rightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
		Hand hand = event.getHand();
		if (hand == Hand.OFF_HAND) {
			// Wrench should be in main hand
			return;
		}
		if (ToolManager.INSTANCE.canHandleTool(event.getEntityPlayer().getHeldItem(Hand.MAIN_HAND))) {
			BlockState state = event.getWorld().getBlockState(event.getPos());
			if(wrenableBlocks.contains(state.getBlock())){
				Block block = state.getBlock();
				block.onBlockActivated(state, event.getWorld(), event.getPos(), event.getEntityPlayer(), hand, event.getFace(), 0F, 0F, 0F);
				event.setCanceled(true);
				event.setCancellationResult(ActionResult.SUCCESS);
			}
		}
	}

	@SubscribeEvent
	public static void getDigSpeed(PlayerEvent.BreakSpeed event) {
		if (wrenableBlocks.contains(event.getState().getBlock()) && RebornCoreConfig.wrenchRequired) {
			event.setNewSpeed(event.getOriginalSpeed() / 25);
		}
	}
}
