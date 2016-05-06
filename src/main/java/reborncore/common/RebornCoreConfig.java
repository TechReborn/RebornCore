package reborncore.common;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import reborncore.api.power.IPowerConfig;

/**
 * Created by Mark on 20/02/2016.
 */
public class RebornCoreConfig
{

	public static Configuration config;

	private static RebornCoreConfig instance = null;
	public static String CATEGORY_POWER = "power";
	public static String CATEGORY_MISC = "misc";

	protected static boolean enableRF;
	protected static boolean enableEU;
	protected static boolean enableTesla;
	public static int euPerRF;

	public static boolean versionCheck;

	public RebornCoreConfig(File configFile)
	{
		config = new Configuration(configFile);
		config.load();

		RebornCoreConfig.Configs();

		config.save();
	}

	public static RebornCoreConfig initialize(File configFile)
	{

		if (instance == null)
			instance = new RebornCoreConfig(configFile);
		else
			throw new IllegalStateException("Cannot initialize TechReborn Config twice");

		return instance;
	}

	public static RebornCoreConfig instance()
	{
		if (instance == null)
		{

			throw new IllegalStateException("Instance of TechReborn Config requested before initialization");
		}
		return instance;
	}

	public static void Configs()
	{
		enableRF = config.get(CATEGORY_POWER, "Allow RF", false, "Allow machines to be powered with RF").getBoolean();

		enableTesla = config.get(CATEGORY_POWER, "Allow Tesla", false, "Allow machines to be powered with Tesla").getBoolean();

		// enableEU = config
		// .get(CATEGORY_POWER, "Allow EU", Loader.isModLoaded("IC2"), "Allow
		// machines to be powered with EU")
		// .getBoolean();
		enableEU = true;

		euPerRF = config.get(CATEGORY_POWER, "EU - RF ratio", 4, "The Amount of RF to output from EU").getInt();

		versionCheck = config.get(CATEGORY_MISC, "Check for new versions", true, "Enable version checker").getBoolean();

		//resets this when the config is reloaded
		powerConfig = null;
	}

	private static IPowerConfig powerConfig = null;

	public static IPowerConfig getRebornPower(){
		if(powerConfig == null){
			powerConfig = new IPowerConfig() {
				@Override
				public boolean eu() {
					return enableEU;
				}

				@Override
				public boolean rf() {
					return enableRF;
				}

				@Override
				public boolean tesla() {
					return enableTesla;
				}

				@Override
				public boolean internal() {
					return true;
				}
			};
		}
		return powerConfig;
	}
}
