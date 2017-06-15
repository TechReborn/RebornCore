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

package reborncore.mixin.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mark on 02/01/2017.
 */
public class JsonUtil {

	public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static MixinConfiguration mixinConfigurationFromFile(File file) throws IOException {
		return mixinConfigurationFromString(FileUtils.readFileToString(file));
	}

	public static MixinConfiguration mixinConfigurationFromString(String json) {
		return GSON.fromJson(json, MixinConfiguration.class);
	}

	public static void main(String[] args) throws IOException {
		MixinConfiguration configuration = new MixinConfiguration();
		configuration.mixinData = new ArrayList<>();
		configuration.mixinData.add(new MixinTargetData("mixinclass", "targetclass"));
		configuration.mixinData.add(new MixinTargetData("mixinclass2", "targetclass2"));
		String json = GSON.toJson(configuration);
		FileUtils.writeStringToFile(new File("mixinConfig.json"), json);
	}

}
