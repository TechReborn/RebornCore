package teamreborn.reborncore.manual;

import java.util.ArrayList;
import java.util.List;

/**
 * File Created by Prospector.
 */
public class ManualRegistry {
	private static List<String> modsWithManuals = new ArrayList<>();

	public static void addMod(String modid) {
		modsWithManuals.add(modid);
	}

	public static List<String> getModsWithManuals() {
		return modsWithManuals;
	}
}
