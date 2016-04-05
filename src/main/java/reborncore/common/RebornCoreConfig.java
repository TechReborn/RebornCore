package reborncore.common;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

/**
 * Created by Mark on 20/02/2016.
 */
public class RebornCoreConfig {

    public static Configuration config;

    private static RebornCoreConfig instance = null;
    public static String CATEGORY_POWER = "power";

    public static boolean enableRF;
    public static boolean enableEU;
    public static int euPerRF;

    public RebornCoreConfig(File configFile) {
        config = new Configuration(configFile);
        config.load();

        RebornCoreConfig.Configs();

        config.save();
    }

    public static RebornCoreConfig initialize(File configFile) {

        if (instance == null)
            instance = new RebornCoreConfig(configFile);
        else
            throw new IllegalStateException("Cannot initialize TechReborn Config twice");

        return instance;
    }

    public static RebornCoreConfig instance() {
        if (instance == null) {

            throw new IllegalStateException("Instance of TechReborn Config requested before initialization");
        }
        return instance;
    }

    public static void Configs() {
        enableRF = config
                .get(CATEGORY_POWER, "Allow RF", false, "Allow machines to be powered with RF")
                .getBoolean();

        enableEU = config
                .get(CATEGORY_POWER, "Allow EU", Loader.isModLoaded("IC2"), "Allow machines to be powered with EU")
                .getBoolean();

        euPerRF = config.get(CATEGORY_POWER, "EU - RF ratio", 4, "The Amount of RF to output from EU").getInt();
    }
}
