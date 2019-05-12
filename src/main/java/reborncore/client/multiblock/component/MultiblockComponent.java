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

/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */

package reborncore.client.multiblock.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiblockComponent {

	public BlockPos relPos;
	public final BlockState state;

	public MultiblockComponent(BlockPos relPos, BlockState state) {
		this.relPos = relPos;
		this.state = state;
	}

	public BlockPos getRelativePosition() {
		return relPos;
	}

	public Block getBlock() {
		return state.getBlock();
	}

	public BlockState getState() {
		return state;
	}

	public boolean matches(World world, int x, int y, int z) {
		return world.getBlockState(new BlockPos(x, y, z)).getBlock() == getBlock();
	}

	public void rotate(double angle) {
		double x = relPos.getX();
		double z = relPos.getZ();
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);

		double xn = x * cos - z * sin;
		double zn = x * sin + z * cos;
		relPos = new BlockPos((int) Math.round(xn), relPos.getY(), (int) Math.round(zn));
	}

	public MultiblockComponent copy() {
		return new MultiblockComponent(relPos, state);
	}
}
