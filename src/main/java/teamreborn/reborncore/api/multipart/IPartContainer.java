package teamreborn.reborncore.api.multipart;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Inital part container.
 */
public interface IPartContainer
{

	public World getWorld();

	public BlockPos getPos();

	public IBlockState getState();
}
