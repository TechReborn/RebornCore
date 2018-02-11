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

package reborncore.common.misc.vecmath;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class Vecs3dCube {

	private Vecs3d min, max;

	public Vecs3dCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {

		this(minX, minY, minZ, maxX, maxY, maxZ, (World) null);
	}

	public Vecs3dCube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, World world) {

		this(new Vecs3d(minX, minY, minZ, world), new Vecs3d(maxX, maxY, maxZ, world));
	}

	public Vecs3dCube(Vecs3d a, Vecs3d b) {

		World w = a.getWorld();
		if (w == null)
			w = b.getWorld();

		min = a;
		max = b;

		fix();
	}

	public Vecs3dCube(AxisAlignedBB aabb) {

		this(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
	}

	public Vecs3d getMin() {

		return min;
	}

	public Vecs3d getMax() {

		return max;
	}

	public Vecs3d getCenter() {

		return new Vecs3d((getMinX() + getMaxX()) / 2D, (getMinY() + getMaxY()) / 2D, (getMinZ() + getMaxZ()) / 2D,
			getMin().getWorld());
	}

	public double getMinX() {

		return min.getX();
	}

	public double getMinY() {

		return min.getY();
	}

	public double getMinZ() {

		return min.getZ();
	}

	public double getMaxX() {

		return max.getX();
	}

	public double getMaxY() {

		return max.getY();
	}

	public double getMaxZ() {

		return max.getZ();
	}

	public AxisAlignedBB toAABB() {

		return new AxisAlignedBB(getMinX(), getMinY(), getMinZ(), getMaxX(), getMaxY(), getMaxZ());
	}

	@Override
	public Vecs3dCube clone() {

		return new Vecs3dCube(min.clone(), max.clone());
	}

	public Vecs3dCube expand(double size) {

		min.sub(size, size, size);
		max.add(size, size, size);

		return this;
	}

	public Vecs3dCube fix() {

		Vecs3d a = min.clone();
		Vecs3d b = max.clone();

		double minX = Math.min(a.getX(), b.getX());
		double minY = Math.min(a.getY(), b.getY());
		double minZ = Math.min(a.getZ(), b.getZ());

		double maxX = Math.max(a.getX(), b.getX());
		double maxY = Math.max(a.getY(), b.getY());
		double maxZ = Math.max(a.getZ(), b.getZ());

		min = new Vecs3d(minX, minY, minZ, a.w);
		max = new Vecs3d(maxX, maxY, maxZ, b.w);

		return this;
	}

	public Vecs3dCube add(double x, double y, double z) {

		min.add(x, y, z);
		max.add(x, y, z);

		return this;
	}

	public static final Vecs3dCube merge(List<Vecs3dCube> cubes) {

		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		double maxx = Double.MIN_VALUE;
		double maxy = Double.MIN_VALUE;
		double maxz = Double.MIN_VALUE;

		for (Vecs3dCube c : cubes) {
			minx = Math.min(minx, c.getMinX());
			miny = Math.min(miny, c.getMinY());
			minz = Math.min(minz, c.getMinZ());
			maxx = Math.max(maxx, c.getMaxX());
			maxy = Math.max(maxy, c.getMaxY());
			maxz = Math.max(maxz, c.getMaxZ());
		}

		if (cubes.size() == 0)
			return new Vecs3dCube(0, 0, 0, 0, 0, 0);

		return new Vecs3dCube(minx, miny, minz, maxx, maxy, maxz);
	}

	@Override
	public int hashCode() {

		return min.hashCode() << 8 + max.hashCode();
	}
}
