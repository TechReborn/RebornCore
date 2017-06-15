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

package reborncore.common.misc;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import reborncore.common.util.WorldUtils;

/**
 * Based off https://github.com/SteamNSteel/SteamNSteel
 */
public class ChunkCoord implements Comparable<ChunkCoord> {
	private final ImmutablePair<Integer, Integer> data;

	private ChunkCoord(int x, int z) {
		data = ImmutablePair.of(x, z);
	}

	private ChunkCoord(ChunkEvent event) {
		this(event.getChunk().x, event.getChunk().z);
	}

	public static ChunkCoord of(int x, int z) {
		return new ChunkCoord(x, z);
	}

	public static ChunkCoord of(BlockPos coord) {
		return new ChunkCoord(coord.getX() >> 4, coord.getZ() >> 4);
	}

	public static ChunkCoord of(ChunkEvent event) {
		return new ChunkCoord(event);
	}

	public boolean exists(World world) {
		return WorldUtils.chunkExists(world, data.getLeft(), data.getRight());
	}

	public boolean containsWorldCoord(BlockPos coord) {
		return equals(of(coord));
	}

	public int getX() {
		return data.left;
	}

	public int getZ() {
		return data.right;
	}

	public BlockPos localToWorldCoords(BlockPos coord) {
		return new BlockPos((data.left << 4) + coord.getX(), coord.getY(), (data.right << 4) + coord.getZ());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(data.left, data.right);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final ChunkCoord that = (ChunkCoord) o;
		return data.left.equals(that.data.left) && data.right.equals(that.data.right);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("X", data.left).add("Z", data.right).toString();
	}

	@Override
	public int compareTo(ChunkCoord o) {
		return data.left.equals(o.data.left) ? data.right.compareTo(o.data.right) : data.left.compareTo(o.data.left);
	}
}
