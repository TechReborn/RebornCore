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

package reborncore.common.config;

import java.lang.reflect.Field;
import java.util.*;

public class Configuration {

	Class clazz;
	String modId;

	public Configuration(Class clazz, String modId) {
		this.clazz = clazz;
		this.modId = modId;
		setup();
	}

	public static Map<String, ConfigurationWrapper> configMap = new HashMap<>();

	private void setup() {
		Arrays.stream(clazz.getDeclaredFields()).forEach(field -> handleField(field));
		configMap.forEach((s, configuration) -> configuration.save());
	}

	private void handleField(Field field){
		if(!field.isAnnotationPresent(Config.class)){
			return;
		}
		try {
			Config annotation = field.getAnnotation(Config.class);

			String key = annotation.key();
			if(key.isEmpty()){
				key = field.getName();
			}

			String category = annotation.category();

			ConfigurationWrapper configuration = configMap.get(modId);
			if(configuration == null){
				configuration = new ConfigurationWrapper(modId);
				configMap.put(modId, configuration);
			}

			Object defaultValue = field.get(null);

			Object value = configuration
				.getNode(category, key)
				.setComment(annotation.comment())
				.getValue(defaultValue);

			field.set(null, value);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
