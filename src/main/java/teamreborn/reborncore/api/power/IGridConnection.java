package teamreborn.reborncore.api.power;

import teamreborn.reborncore.grid.PowerGrid;

import javax.annotation.Nullable;

/**
 * Used on a tile to indicate that it should be connected to the power grid.
 */
public interface IGridConnection {

	@Nullable
	public PowerGrid getPowerGrid();

	public void setPowerGrid(@Nullable PowerGrid powerGrid);

}
