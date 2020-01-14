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


package reborncore.common.explosion;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.StopWatch;
import reborncore.RebornCore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by modmuss50 on 12/03/2016.
 */
public class RebornExplosion extends Explosion {

	@Nonnull
	BlockPos center;

	@Nonnull
	World world;

	@Nonnull
	int radius;

	@Nullable
	EntityLivingBase livingBase;

	public RebornExplosion(
		@Nonnull
			BlockPos center,
		@Nonnull
			World world,
		@Nonnull
			int radius) {
		super(world, null, center.getX(), center.getY(), center.getZ(), radius, false, true);
		this.center = center;
		this.world = world;
		this.radius = radius;
	}

	public void setLivingBase(
		@Nullable
			EntityLivingBase livingBase) {
		this.livingBase = livingBase;
	}

	public
	@Nullable
	EntityLivingBase getLivingBase() {
		return livingBase;
	}

	public void explode() {
		StopWatch watch = new StopWatch();
		watch.start();
		for (int tx = -radius; tx < radius + 1; tx++) {
			for (int ty = -radius; ty < radius + 1; ty++) {
				for (int tz = -radius; tz < radius + 1; tz++) {
					if (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)) <= radius - 2) {
						BlockPos pos = center.add(tx, ty, tz);
						IBlockState state = world.getBlockState(pos);
						Block block = state.getBlock();
						if (block != Blocks.BEDROCK && block != Blocks.AIR) {
							block.onBlockExploded(world, pos, this);
							world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				}
			}
		}
		RebornCore.logHelper.info("The explosion took" + watch + " to explode");
	}

	@Override
	public void doExplosionA() {
		explode();
	}

	@Override
	public void doExplosionB(boolean spawnParticles) {
		explode();
	}

	@Override
	public
	@Nullable
	EntityLivingBase getExplosivePlacedBy() {
		return livingBase;
	}

	@Override
	public List<BlockPos> getAffectedBlockPositions() {
		List<BlockPos> poses = new ArrayList<>();
		for (int tx = -radius; tx < radius + 1; tx++) {
			for (int ty = -radius; ty < radius + 1; ty++) {
				for (int tz = -radius; tz < radius + 1; tz++) {
					if (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)) <= radius - 2) {
						BlockPos pos = center.add(tx, ty, tz);
						IBlockState state = world.getBlockState(pos);
						Block block = state.getBlock();
						if (block != Blocks.BEDROCK && block != Blocks.AIR) {
							poses.add(pos);
						}
					}
				}
			}
		}
		return poses;
	}
}
