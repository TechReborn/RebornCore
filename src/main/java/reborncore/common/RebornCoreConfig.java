package reborncore.common;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
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
	protected static boolean enableForge;
	public static int euPriority;
	public static int teslaPriority;
	public static int forgePriority;
	public static int euPerRF;
	public static int euPerFU;

	public static boolean versionCheck;
	public static boolean easterEggs;

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

		enableForge = config.get(CATEGORY_POWER, "Allow Forge", true, "Allow machines to be powered with Forges power system").getBoolean();

		 enableEU = config
		 .get(CATEGORY_POWER, "Allow EU", Loader.isModLoaded("IC2"), "Allow machines to be powered with EU")
		 .getBoolean();

		euPerRF = config.get(CATEGORY_POWER, "EU - RF ratio", 4, "The Amount of RF to output from EU").getInt();
		euPerFU = config.get(CATEGORY_POWER, "EU - FU ratio", 4, "The Amount of FU to output from EU").getInt();

		versionCheck = config.get(CATEGORY_MISC, "Check for new versions", true, "Enable version checker").getBoolean();

		easterEggs = config.get(CATEGORY_MISC, "Enable Seasonal Easter Eggs", true, "Disable this is you don't want seasonal easter eggs").getBoolean();

		euPriority = config.get(CATEGORY_POWER, "EU Priority", 1, "Priority of EU for display purposes. Higher number = higher priority (ClientSideOnly)").getInt();
		teslaPriority = config.get(CATEGORY_POWER, "TESLA Priority", 2, "Priority of TESLA for display purposes. Higher number = higher priority (ClientSideOnly)").getInt();
		forgePriority = config.get(CATEGORY_POWER, "Forge Energy Priority", 0, "Priority of FE/FU/Fwhatever for display purposes. Higher number = higher priority (ClientSideOnly)").getInt();

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
					if(enableForge){
						//Its the same, lets be honest
						return true;
					}
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

				@Override
				public boolean forge() {
					return enableForge;
				}
			};
		}
		return powerConfig;
	}
}
