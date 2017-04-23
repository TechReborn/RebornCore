package teamreborn.reborncore.grid;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import teamreborn.reborncore.api.power.GridJoinEvent;
import teamreborn.reborncore.api.power.GridLeaveEvent;
import teamreborn.reborncore.api.power.IGridConnection;
import teamreborn.reborncore.api.registry.RebornRegistry;
import teamreborn.reborncore.api.registry.impl.EventRegistry;
import teamreborn.techreborn.TRConstants;

import java.util.HashMap;

/**
 * Created by Mark on 22/04/2017.
 */
@RebornRegistry(TRConstants.MOD_ID)
@EventRegistry
public class GridEventHandler {

	public static HashMap<Integer, GridWorldManager> worldManagerHashMap = new HashMap<>();

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			event.world.theProfiler.startSection("TechReborn Powernet");
			getWorldManagerFromID(event.world.provider.getDimension()).tick(event);
			event.world.theProfiler.endSection();
		}
	}

	@SubscribeEvent
	public void gridJoin(GridJoinEvent event) {
		if (event.getWorld().isRemote) {
			return;
		}
		joinOrCreatePowerGrid(event.getWorld(), event.getPos(), event.getConnection());
	}

	@SubscribeEvent
	public void gridLeave(GridLeaveEvent event) {
		leaveAndSplit(event.getWorld(), event.getPos(), event.getConnection());
	}

	public static GridWorldManager getWorldManagerFromID(int worldID) {
		if (worldManagerHashMap.containsKey(worldID)) {
			return worldManagerHashMap.get(worldID);
		} else {
			GridWorldManager gridWorldManager = new GridWorldManager();
			worldManagerHashMap.put(worldID, gridWorldManager);
			return gridWorldManager;
		}
	}

	public PowerGrid joinOrCreatePowerGrid(World world, BlockPos pos, IGridConnection gridConnection) {
		GridWorldManager gridWorldManager = getWorldManagerFromID(world.provider.getDimension());
		return gridWorldManager.joinOrCreatePowerGrid(world, pos, gridConnection);
	}

	public void leaveAndSplit(World world, BlockPos pos, IGridConnection gridConnection) {
		GridWorldManager gridWorldManager = getWorldManagerFromID(world.provider.getDimension());
		gridWorldManager.leaveAndSplit(world, pos, gridConnection);
	}

}
