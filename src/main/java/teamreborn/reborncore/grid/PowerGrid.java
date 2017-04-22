package teamreborn.reborncore.grid;

import teamreborn.reborncore.api.power.IGridConnection;

import java.util.ArrayList;
import java.util.List;

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

}
