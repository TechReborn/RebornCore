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

package reborncore.common.multiblock.rectangular;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.multiblock.CoordTriplet;
import reborncore.common.multiblock.MultiblockControllerBase;
import reborncore.common.multiblock.MultiblockValidationException;

public abstract class RectangularMultiblockControllerBase extends MultiblockControllerBase {

	protected RectangularMultiblockControllerBase(World world) {
		super(world);
	}

	/**
	 * @return True if the machine is "whole" and should be assembled. False
	 * otherwise.
	 */
	protected void isMachineWhole() throws MultiblockValidationException {
		if (connectedParts.size() < getMinimumNumberOfBlocksForAssembledMachine()) {
			throw new MultiblockValidationException("Machine is too small.");
		}

		CoordTriplet maximumCoord = getMaximumCoord();
		CoordTriplet minimumCoord = getMinimumCoord();

		// Quickly check for exceeded dimensions
		int deltaX = maximumCoord.x - minimumCoord.x + 1;
		int deltaY = maximumCoord.y - minimumCoord.y + 1;
		int deltaZ = maximumCoord.z - minimumCoord.z + 1;

		int maxX = getMaximumXSize();
		int maxY = getMaximumYSize();
		int maxZ = getMaximumZSize();
		int minX = getMinimumXSize();
		int minY = getMinimumYSize();
		int minZ = getMinimumZSize();

		if (maxX > 0 && deltaX > maxX) {
			throw new MultiblockValidationException(
				String.format("Machine is too large, it may be at most %d blocks in the X dimension", maxX));
		}
		if (maxY > 0 && deltaY > maxY) {
			throw new MultiblockValidationException(
				String.format("Machine is too large, it may be at most %d blocks in the Y dimension", maxY));
		}
		if (maxZ > 0 && deltaZ > maxZ) {
			throw new MultiblockValidationException(
				String.format("Machine is too large, it may be at most %d blocks in the Z dimension", maxZ));
		}
		if (deltaX < minX) {
			throw new MultiblockValidationException(
				String.format("Machine is too small, it must be at least %d blocks in the X dimension", minX));
		}
		if (deltaY < minY) {
			throw new MultiblockValidationException(
				String.format("Machine is too small, it must be at least %d blocks in the Y dimension", minY));
		}
		if (deltaZ < minZ) {
			throw new MultiblockValidationException(
				String.format("Machine is too small, it must be at least %d blocks in the Z dimension", minZ));
		}

		// Now we run a simple check on each block within that volume.
		// Any block deviating = NO DEAL SIR
		TileEntity te;
		RectangularMultiblockTileEntityBase part;
		Class<? extends RectangularMultiblockControllerBase> myClass = this.getClass();

		for (int x = minimumCoord.x; x <= maximumCoord.x; x++) {
			for (int y = minimumCoord.y; y <= maximumCoord.y; y++) {
				for (int z = minimumCoord.z; z <= maximumCoord.z; z++) {
					// Okay, figure out what sort of block this should be.

					te = this.worldObj.getTileEntity(new BlockPos(x, y, z));
					if (te instanceof RectangularMultiblockTileEntityBase) {
						part = (RectangularMultiblockTileEntityBase) te;

						// Ensure this part should actually be allowed within a
						// cube of this controller's type
						if (!myClass.equals(part.getMultiblockControllerType())) {
							throw new MultiblockValidationException(
								String.format("Part @ %d, %d, %d is incompatible with machines of type %s", x, y, z,
									myClass.getSimpleName()));
						}
					} else {
						// This is permitted so that we can incorporate certain
						// non-multiblock parts inside interiors
						part = null;
					}

					// Validate block type against both part-level and
					// material-level validators.
					int extremes = 0;
					if (x == minimumCoord.x) {
						extremes++;
					}
					if (y == minimumCoord.y) {
						extremes++;
					}
					if (z == minimumCoord.z) {
						extremes++;
					}

					if (x == maximumCoord.x) {
						extremes++;
					}
					if (y == maximumCoord.y) {
						extremes++;
					}
					if (z == maximumCoord.z) {
						extremes++;
					}

					if (extremes >= 2) {
						if (part != null) {
							part.isGoodForFrame();
						} else {
							isBlockGoodForFrame(this.worldObj, x, y, z);
						}
					} else if (extremes == 1) {
						if (y == maximumCoord.y) {
							if (part != null) {
								part.isGoodForTop();
							} else {
								isBlockGoodForTop(this.worldObj, x, y, z);
							}
						} else if (y == minimumCoord.y) {
							if (part != null) {
								part.isGoodForBottom();
							} else {
								isBlockGoodForBottom(this.worldObj, x, y, z);
							}
						} else {
							// Side
							if (part != null) {
								part.isGoodForSides();
							} else {
								isBlockGoodForSides(this.worldObj, x, y, z);
							}
						}
					} else {
						if (part != null) {
							part.isGoodForInterior();
						} else {
							isBlockGoodForInterior(this.worldObj, x, y, z);
						}
					}
				}
			}
		}
	}

}
