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
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiblockComponent {

	public BlockPos relPos;
	public final IBlockState state;

	public MultiblockComponent(BlockPos relPos, IBlockState state) {
		this.relPos = relPos;
		this.state = state;
	}

	public BlockPos getRelativePosition() {
		return relPos;
	}

	public Block getBlock() {
		return state.getBlock();
	}

	public IBlockState getState() {
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
