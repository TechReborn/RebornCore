package teamreborn.reborncore.api.power;

/**
 * Used on a tile that can recieve power from the grid.
 */
public interface IGridReciever extends IGridConnection {

	/**
	 * This is called from the power grid, this is where the progress should be updated, but do not handle things like inventory interaction from here.
	 *
	 * @param power this is the amount of power that the machine has been give, note it can be less than the requested power. Can even be 0 but will near be less than 0
	 * @param powerRatio This would be 1.0 if the machine recieved all of its requested power, but only 0.25 if the grid does not have enough power to go around.
	 */
	public void handlePower(double power, double powerRatio);

	/**
	 * This is used to request the maximum ammout of power the machine should run off, this should ruturn 0 if the machine is not running.
	 *
	 * @return The maximum amount of power the machine could use at full effiecnecy
	 */
	public double requestPower();

}
