package teamreborn.reborncore.api.power;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Mark on 22/04/2017.
 */
public class GridJoinEvent extends Event {

	World world;
	BlockPos pos;
	IGridConnection connection;

	public GridJoinEvent(World world, BlockPos pos, IGridConnection connection) {
		this.world = world;
		this.pos = pos;
		this.connection = connection;
	}

	public World getWorld() {
		return world;
	}

	public BlockPos getPos() {
		return pos;
	}

	public IGridConnection getConnection() {
		return connection;
	}
}
