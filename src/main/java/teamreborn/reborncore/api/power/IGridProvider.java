package teamreborn.reborncore.api.power;

/**
 * Used on a tile entity that should provide power to the grid.
 */
public interface IGridProvider {

	/**
	 *
	 * @return The amount of power this machine can provide. Should be 0 if the provider has nothing to provide.
	 */
	public double providePower();

}
