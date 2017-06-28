/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
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

package reborncore.mixin;

import org.apache.logging.log4j.Logger;
import reborncore.mixin.api.IMixinRegistry;
import reborncore.mixin.json.MixinConfiguration;
import reborncore.mixin.json.MixinTargetData;
import reborncore.mixin.transformer.IMixinRemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ServiceLoader;

public class MixinManager {

	public static List<String> mixinClassList = new ArrayList<>();
	public static HashMap<String, List<String>> mixinTargetMap = new HashMap<>();

	public static List<String> transformedClasses = new ArrayList<>();

	//The remapper instance
	public static IMixinRemap mixinRemaper;
	//The logger
	public static Logger logger;

	public static void load() {
		List<IMixinRegistry> registries = new ArrayList<>();
		registries.addAll(load(MixinManager.class.getClassLoader()));
		List<MixinTargetData> dataList = new ArrayList<>();
		for (IMixinRegistry registry : registries) {
			dataList.addAll(registry.register());
		}
		dataList.forEach(MixinManager::registerMixin);
		logger.info("Registed " + dataList.size() + " mixins");
	}

	private static List<IMixinRegistry> load(ClassLoader classLoader) {
		ServiceLoader<IMixinRegistry> serviceLoader = ServiceLoader.load(IMixinRegistry.class, classLoader);
		List<IMixinRegistry> registries = new ArrayList<>();
		for (IMixinRegistry mixinRegistry : serviceLoader) {
			registries.add(mixinRegistry);
		}
		return registries;
	}

	//Use service loader now
	private static void registerMixin(MixinTargetData data) {
		mixinClassList.add(data.mixinClass);
		if (mixinTargetMap.containsKey(data.targetClass)) {
			mixinTargetMap.get(data.targetClass).add(data.mixinClass);
		} else {
			List<String> list = new ArrayList<>();
			list.add(data.mixinClass);
			mixinTargetMap.put(data.targetClass, list);
		}
	}

	public static void registerMixinConfig(MixinConfiguration configuration) {
		for (MixinTargetData mixindata : configuration.mixinData) {
			registerMixin(mixindata);
		}
	}
}
