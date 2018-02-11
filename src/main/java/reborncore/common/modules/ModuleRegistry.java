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

package reborncore.common.modules;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashMap;

/**
 * @author Prospector on 28/05/16
 */
public class ModuleRegistry {
	public HashMap<Module, String> modules = new HashMap<Module, String>();

	public void addModules() {
		checkRequirements();
	}

	protected void checkRequirements() {
		for (Module i : modules.keySet())
			hasRequirements(i);
	}

	public void preInit(FMLPreInitializationEvent event) {
		addModules();
		for (Module i : modules.keySet()) {
			if (i.isActive)
				i.preInit(event);
		}
	}

	public void init(FMLInitializationEvent event) {
		for (Module i : modules.keySet()) {
			if (i.isActive)
				i.init(event);
		}
	}

	public void postInit(FMLPostInitializationEvent event) {
		for (Module i : modules.keySet()) {
			if (i.isActive)
				i.postInit(event);
		}
	}

	public boolean hasRequirements(Module module) {
		String[] reqMods = module.getModsRequired();
		String[] reqModules = module.getModulesRequired();

		if (reqMods != null)
			for (String i : reqMods) {
				if (!Loader.isModLoaded(i)) {
					module.isActive = false;
					return false;
				}
			}

		if (reqModules != null)
			for (String i : reqModules) {
				if (!modules.values().contains(i)) {
					module.isActive = false;
					return false;
				}
			}

		return true;
	}
}
