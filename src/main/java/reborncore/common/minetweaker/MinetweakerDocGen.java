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

package reborncore.common.minetweaker;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.commons.io.FileUtils;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Mark on 19/02/2017.
 */
public class MinetweakerDocGen {

	public static void gen(ASMDataTable dataTable, File exportFile) {
		long start = System.currentTimeMillis();
		if (exportFile.exists()) {
			exportFile.delete();
		}
		StringBuilder builder = new StringBuilder();
		String newline = System.getProperty("line.separator");
		List<String> lines = new ArrayList<>();
		Set<ASMDataTable.ASMData> asmDataSet = dataTable.getAll(ZenClass.class.getCanonicalName());
		for (ASMDataTable.ASMData asmData : asmDataSet) {
			if (asmData.getAnnotationInfo().size() != 0) {
				String prefix = (String) asmData.getAnnotationInfo().get("value");
				if (prefix.startsWith("minetweaker")) {
					//Skipping MT as it contains a bunch of useless info, use their wiki for this.
					continue;
				}
				try {
					Class clazz = Class.forName(asmData.getClassName());
					if (clazz.getMethods().length == 0) { //TODO check zen methods
						continue;
					}
					for (Method method : clazz.getMethods()) {
						if (method.isAnnotationPresent(ZenMethod.class)) {
							StringBuilder parameter = new StringBuilder();
							parameter.append("(");
							for (int i = 0; i < method.getParameterTypes().length; i++) {
								Class param = method.getParameterTypes()[i];
								parameter.append(param.getSimpleName());
								if (i + 1 != method.getParameterTypes().length) {
									parameter.append(", ");
								}
							}
							parameter.append(")");
							lines.add(prefix + "." + method.getName() + parameter.toString());
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(lines);
		for (String line : lines) {
			builder.append(line);
			builder.append(newline);
		}

		builder.append("Generated by reborn core in " + (System.currentTimeMillis() - start) + "ms");
		try {
			FileUtils.writeStringToFile(exportFile, builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
