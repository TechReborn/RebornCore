package teamreborn.reborncore.util;

import net.minecraft.util.math.AxisAlignedBB;

/**
 * Created by Prospector
 */
public class AABBUtils {
	public static AxisAlignedBB rotate(AxisAlignedBB bounds, AxisRotation rot) {
		return new AxisAlignedBB(
			rot.getX(bounds.minX, bounds.minZ), rot.getX(bounds.maxX, bounds.maxZ),
			bounds.minY, bounds.maxY,
			rot.getZ(bounds.minX, bounds.minZ), rot.getZ(bounds.maxX, bounds.maxZ)
		);
	}

	public enum AxisRotation {
		NONE(1, 0, 0, 1),
		CLOCKWISE_90(0, 1, -1, 0),
		CLOCKWISE_180(-1, 0, 0, -1),
		COUNTERCLOCKWISE_90(0, -1, 1, 0);

		private final double xx, xz, zx, zz;

		AxisRotation(double xx, double xz, double zx, double zz) {
			this.xx = xx;
			this.xz = xz;
			this.zx = zx;
			this.zz = zz;
		}

		public final double getX(double x, double z) {
			return x * xx + z * xz;
		}

		public final double getZ(double x, double z) {
			return x * zx + z * zz;
		}
	}
}
