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

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.RebornCore;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RebornRegister;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@IRegistryFactory.RegistryFactory
public class ConfigRegistryFactory implements IRegistryFactory {

	String modId;

	List<RebornConfig> configs = new ArrayList<>();

	@Override
	public void handleField(String modId, Field field) {
		RebornRegister rebornRegistry = (RebornRegister) RegistrationManager.getAnnoation(field.getDeclaringClass().getAnnotations(), RebornRegister.class);

		Validate.notBlank(modId);
		Validate.notBlank(rebornRegistry.value());
		Validate.isTrue(!rebornRegistry.value().equals("null"));

		Validate.isTrue(rebornRegistry.value().equals(modId));

		ConfigRegistry annotation = (ConfigRegistry) RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
		RebornConfig config = getConfig(annotation);

		RebornConfig.ConfigEntry configEntry = new RebornConfig.ConfigEntry(annotation.key(), annotation.comment(), field);

		if (config.entryies.containsKey(annotation.config())) {
			config.entryies.get(annotation.config()).add(configEntry);
		} else {
			List<RebornConfig.ConfigEntry> configEntries = new ArrayList<>();
			configEntries.add(configEntry);
			config.entryies.put(annotation.config(), configEntries);
		}
	}

	RebornConfig getConfig(ConfigRegistry config) {
		String name = modId + "_" + config.config();
		RebornConfig rebornConfig = configs.stream().filter(rebornConfig1 -> (rebornConfig1.modid + "_" + rebornConfig1.configName).equals(name)).findFirst().orElse(null);
		if (rebornConfig != null) {
			return rebornConfig;
		}
		rebornConfig = new RebornConfig(modId, config.config());
		configs.add(rebornConfig);
		return rebornConfig;
	}

	@Override
	public void factoryComplete() {
		RebornCore.LOGGER.info("Loading " + configs.size() + " configs for " + modId);
		configs.forEach(rebornConfig -> {
			Pair<ConfigRegistryFactory, ForgeConfigSpec> configSpec = new ForgeConfigSpec.Builder().configure(builder -> buildConfigs(builder, rebornConfig));
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, configSpec.getRight(), rebornConfig.getName() + ".toml");
		});
	}

	private ConfigRegistryFactory buildConfigs(ForgeConfigSpec.Builder builder, RebornConfig config) {
		config.entryies.forEach((key, value1) -> {
			builder.push(key);
			value1.forEach(configEntry -> {
				if (!configEntry.comment.isEmpty()) {
					builder.comment(configEntry.comment);
				}
				configEntry.value = builder.define(configEntry.key, configEntry.getDefault());
			});
			builder.pop();
		});
		return this;
	}

	public RebornConfig fromModConfig(ModConfig modConfig) {
		return configs.stream()
			.filter(config -> modConfig.getFileName().endsWith(config.getName() + ".toml"))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Failed to find config: " + modConfig.getFileName()));
	}

	public void populateConfigs(RebornConfig config) {
		System.out.println("populating configs");
		config.getAll().forEach(configEntry -> {
			try {
				System.out.println("Updating config entry " + configEntry.key + " new value: " + configEntry.value.get());
				configEntry.field.set(null, configEntry.value.get());
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Failed to set config value", e);
			}
		});
	}

	@Override
	public List<RegistryTarget> getTargets() {
		return Collections.singletonList(RegistryTarget.FIELD);
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return ConfigRegistry.class;
	}

	@Override
	public void onInit(String modId) {
		this.modId = modId;
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener((Consumer<ModConfig.ModConfigEvent>) event -> {
			if (event.getConfig().getModId().equals(modId)) {
				populateConfigs(fromModConfig(event.getConfig()));
			}
		});
		//		eventBus.addListener((Consumer<ConfigChangedEvent.OnConfigChangedEvent>) event -> {
		//			if(event.getModID().equals(modId)){
		//				populateAllConfigs();
		//			}
		//		});
	}
}
