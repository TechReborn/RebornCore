package teamreborn.reborncore.grid;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import teamreborn.reborncore.api.power.IGridConnection;
import teamreborn.reborncore.api.power.IGridProvider;
import teamreborn.reborncore.api.power.IGridReciever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark on 22/04/2017.
 */
public class PowerGrid {

	public String name;

	List<IGridConnection> connections = new ArrayList<>();

	public PowerGrid(String name) {
		this.name = name;
	}


	public void addConnection(IGridConnection connection){
		if(!connections.contains(connection)){
			connections.add(connection);
		}
	}

	public void tick(TickEvent.WorldTickEvent event){
		double providedPower = 0;
		double requestedPower = 0;
		HashMap<IGridReciever, Double> recieverHashMap = new HashMap<>();
		for(IGridConnection connection : connections){
			if(connection instanceof IGridProvider){
				providedPower += ((IGridProvider) connection).providePower();
			} else if (connection instanceof IGridReciever){
				IGridReciever reciever = (IGridReciever) connection;
				double rPower = reciever.requestPower();
				requestedPower += rPower;
				recieverHashMap.put(reciever, rPower);
			}
		}

		for(Map.Entry<IGridReciever, Double> gridRecieverEntry : recieverHashMap.entrySet()){
			gridRecieverEntry.getKey().handlePower(Math.min(providedPower / requestedPower * gridRecieverEntry.getValue(), gridRecieverEntry.getValue()), 0);
		}
	}

}
