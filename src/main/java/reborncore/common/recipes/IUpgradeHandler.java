package reborncore.common.recipes;

/**
 * This class isnt designed to be used by other mods, if you want to have upgrades have your
 */
public interface IUpgradeHandler {

	void resetSpeedMulti();

	double getSpeedMultiplier();

	void addPowerMulti(double amount);

	void resetPowerMulti();

	double getPowerMultiplier();

	double getEuPerTick(double baseEu);

	void addSpeedMulti(double amount);

}
