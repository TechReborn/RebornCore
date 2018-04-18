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

package reborncore.common.util;

import com.google.common.base.Charsets;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.io.FileUtils;
import reborncore.common.RebornCoreConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OreRegistationEvent {

	public static Map<String, String> oreModIDMap = new HashMap<>();

	@SubscribeEvent
	public static void oreRegistationEvent(OreDictionary.OreRegisterEvent event){
		if(!RebornCoreConfig.oreDebug){
			return;
		}
		ModContainer modContainer = Loader.instance().activeModContainer();
		oreModIDMap.put(event.getName(), modContainer.getModId());
	}

	public static void loadComplete() {
		if(!RebornCoreConfig.oreDebug){
			return;
		}
		File file = new File("rc_ores.txt");
		StringBuilder stringBuilder = new StringBuilder();
		oreModIDMap.forEach((s, s2) -> {
			stringBuilder.append(s + " was added by " + s2);
			stringBuilder.append("\n");
		});
		try {
			FileUtils.writeStringToFile(file, stringBuilder.toString(), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to export ore data", e);
		}
	}

}
