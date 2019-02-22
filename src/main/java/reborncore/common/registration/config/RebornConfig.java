/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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

	public String getName() {
		Validate.notNull(modid);
		return "teamreborn/" + modid + "/" + configName;
	}

	public List<ConfigEntry> getAll() {
		List<ConfigEntry> list = new ArrayList<>();
		entryies.forEach((s, configEntries) -> list.addAll(configEntries));
		return list;
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

		public Object getDefault() {
			try {
				return field.get(null);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Failed to get default config value", e);
			}
		}
	}

}
