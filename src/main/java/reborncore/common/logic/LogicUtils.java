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

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import reborncore.RebornCore;
import reborncore.RebornRegistry;

/**
 * Created by Gigabit101 on 08/04/2017.
 */
public class LogicUtils {
	//use in your GuiHandler
	public static Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (world.getTileEntity(new BlockPos(x, y, z)) != null && world.getTileEntity(new BlockPos(x, y, z)) instanceof LogicController) {
			LogicController machine = (LogicController) world.getTileEntity(new BlockPos(x, y, z));
			return new LogicContainer(player, machine);
		}
		return null;
	}

	//use in your GuiHandler
	public static Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (world.getTileEntity(new BlockPos(x, y, z)) != null && world.getTileEntity(new BlockPos(x, y, z)) instanceof LogicController) {
			LogicController machine = (LogicController) world.getTileEntity(new BlockPos(x, y, z));
			return new LogicGui(player, machine);
		}
		return null;
	}

	public static void registerLogicController(Block block, LogicController logicController) {
		RebornRegistry.registerBlock(block, logicController.getName());
		GameRegistry.registerTileEntity(logicController.getClass(), logicController.getName());
	}

	public static void openGui(EntityPlayer player, LogicController machine) {
		if (!player.isSneaking()) {
			player.openGui(RebornCore.INSTANCE, 0, machine.getWorld(), machine.getPos().getX(), machine.getPos().getY(), machine.getPos().getZ());
		}
	}
}
