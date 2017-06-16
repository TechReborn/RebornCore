package reborncore.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Added onto an item
 */
public interface IToolHandler {

	/**
	 * Called when a machine is actived with the item that has IToolHandler on it
	 *
	 * @param pos the pos of the block
	 * @param world the world of the block
	 * @param player the player that actived the block
	 * @param side the side that the player actived
	 * @param damage if the tool should be damged, or power taken
	 * @return If the tool can handle being actived on the block, return false when the tool is broken or out of power for example.
	 */
	boolean handleTool(BlockPos pos, World world, EntityPlayer player, EnumFacing side, boolean damage);

}
