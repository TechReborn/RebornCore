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

package reborncore.client.multiblock;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * A set of Multiblock objects for various rotations.
 */
public class MultiblockSet {

	private final Multiblock[] mbs;

	public MultiblockSet(Multiblock[] mbs) {
		this.mbs = mbs;
	}

	public MultiblockSet(Multiblock mb) {
		this(mb.createRotations());
	}

	public Multiblock getForEntity(Entity e) {
		return getForRotation(e.rotationYaw);
	}

	public Multiblock getForRotation(double rotation) {
		int facing = MathHelper.floor(rotation * 4.0 / 360.0 + 0.5) & 3;
		return getForIndex(facing);
	}

	public Multiblock getForIndex(int index) {
		return mbs[index];
	}
}
