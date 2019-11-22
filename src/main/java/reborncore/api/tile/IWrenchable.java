package reborncore.api.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Allows a block to make use of the wrench's removal and rotation functions.
 */
public interface IWrenchable {
    /**
     * Get direction the block is facing.
     *
     * The direction typically refers to the front/main/functionally dominant side of a block.
     *
     * @param world World containing the block.
     * @param pos The block's current position in the world.
     * @return Current block facing.
     */
    EnumFacing getFacing(World world, BlockPos pos);

    /**
     * Determine if the block could face towards the specified direction.
     *
     * Shouldn't actually rotate block, just suggest whether the block could face the requested direction
     *
     * @param world World containing the block.
     * @param pos The block's current position in the world.
     * @param newDirection The face to try, see {@link #getFacing(World, BlockPos)}
     * @param player Player potentially causing the rotation, may be <code>null</code>
     *
     * @return If {@link #setFacing(World, BlockPos, EnumFacing, EntityPlayer)} with the same parameters would succeed
     */
    default boolean canSetFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
        return true;
    }

    /**
     * Set the block's facing to face towards the specified direction.
     *
     * Contrary to {@link net.minecraft.block.Block#rotateBlock(World, BlockPos, EnumFacing)} the block should
     * always face the requested direction after successfully processing this method.
     *
     * @param world World containing the block.
     * @param pos The block's current position in the world.
     * @param newDirection Requested facing, see {@link #getFacing}.
     * @param player Player causing the action, may be null.
     * @return true if successful, false otherwise.
     */
    boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player);
}
