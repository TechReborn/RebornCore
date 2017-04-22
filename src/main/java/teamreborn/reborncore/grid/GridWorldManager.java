package teamreborn.reborncore.grid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import teamreborn.reborncore.api.power.IGridConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Mark on 22/04/2017.
 */
public class GridWorldManager {

	HashMap<String, PowerGrid> powerGridHashMap = new HashMap<>();

	public void tick(TickEvent.WorldTickEvent event){
		for(Map.Entry<String, PowerGrid> entry : powerGridHashMap.entrySet()){
			entry.getValue().tick(event);
		}
	}

	public PowerGrid createNewPowerGrid(){
		PowerGrid powerGrid = new PowerGrid(getNewGridName());
		powerGridHashMap.put(powerGrid.name, powerGrid);
		return powerGrid;
	}

	public static String getNewGridName() {
		Random random = new Random();
		return genHash(LocalDateTime.now().toString() + random.nextFloat() + System.nanoTime());
	}
	private static String genHash(String md5) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}

	protected PowerGrid joinOrCreatePowerGrid(World world, BlockPos pos, IGridConnection gridConnection){
		List<IGridConnection> possibleConnections = new ArrayList<>();
		for(EnumFacing facing : EnumFacing.VALUES){
			BlockPos offsetPos = pos.offset(facing);
			if(world.isBlockLoaded(offsetPos)){
				TileEntity tileEntity = world.getTileEntity(offsetPos);
				if(tileEntity instanceof IGridConnection){
					if(gridConnection.getPowerGrid() == null || gridConnection.getPowerGrid() != null && gridConnection.getPowerGrid() != ((IGridConnection) tileEntity).getPowerGrid()){
						if(!possibleConnections.contains(tileEntity)){
							possibleConnections.add((IGridConnection) tileEntity);
						}
					}
				}
			}
		}
		if(possibleConnections.isEmpty()){
			//We need to make a new powernet here
			PowerGrid powerGrid = createNewPowerGrid();
			powerGrid.addConnection(gridConnection);
			gridConnection.setPowerGrid(powerGrid);
			return powerGrid;

		}
		IGridConnection master = null;
		for(IGridConnection connection : possibleConnections){
			if(connection.getPowerGrid() != null){
				if(master == null || connection.getPowerGrid().connections.size() > master.getPowerGrid().connections.size()){
					master = connection;
				}
			}
		}
		master.getPowerGrid().addConnection(gridConnection);
		gridConnection.setPowerGrid(master.getPowerGrid());
		for(IGridConnection connection : possibleConnections){
			if(connection != master){
				merge(master, connection);
			}
		}
		return master.getPowerGrid();
	}

	public void merge(IGridConnection master, IGridConnection old){
		if(master == old){
			return;
		}
		merge(master.getPowerGrid(), old.getPowerGrid());
		old.setPowerGrid(master.getPowerGrid());
	}


	public void merge(PowerGrid master, PowerGrid old){
		if(master == old){
			return;
		}
		for(IGridConnection connection : old.connections){
			if(!master.connections.contains(connection)){
				master.addConnection(connection);
			}
		}
		powerGridHashMap.remove(old.name);
	}


}
