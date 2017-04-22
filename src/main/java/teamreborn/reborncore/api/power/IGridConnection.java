package teamreborn.reborncore.api.power;

/**
 * Used on a block to indicate that it should be connected to the power grid.
 */
public interface IGridConnection {

	/**
	 * Set to false if the block does not have a tile that needs processing
	 * @return If the block's tile needs handling.
	 */
	public default boolean handleLogic() {
		return true;
	}

}
