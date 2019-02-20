package reborncore.common.registration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RebornConfig {

	@Nonnull
	String modid;
	@Nonnull
	String configName;

	public RebornConfig(String modid, String configName) {
		this.modid = modid;
		this.configName = configName;
		Validate.notBlank(modid);
		Validate.notBlank(configName);
	}

	public Map<String, List<ConfigEntry>> entryies = new HashMap<>();

	public String getName(){
		Validate.notNull(modid);
		return "teamreborn/" + modid + "/" + configName;
	}

	public static class ConfigEntry {
		String key;
		String comment;

		Field field;
		@Nullable
		public ForgeConfigSpec.ConfigValue value;


		public ConfigEntry(String key, String comment, Field field) {
			this.key = key;
			this.comment = comment;
			this.field = field;
		}

		public Object getDefault(){
			try {
				return field.get(null);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Failed to get default config value", e);
			}
		}
	}

}
