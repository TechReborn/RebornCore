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

package reborncore.common.modules;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author Prospector on 28/05/16
 */
public class Module {
	/* Module ID; format as so: modid:moduleName
	 * Example:
	 *	crystek.rawMaterials
	 */
	private String moduleID;

	/* Mod ids that must be present in order to load this module */
	private String[] modsRequired;

	/* Module ids that must be present in order to load this module */
	private String[] modulesRequired;

	public boolean isActive = true;

	public Module(String id) {
		moduleID = id;
	}

	public void preInit(FMLPreInitializationEvent event) {

	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	public String getModuleID() {
		return moduleID;
	}

	public String[] getModsRequired() {
		return modsRequired;
	}

	public void setModsRequired(String... modIds) {
		modsRequired = modIds;
	}

	public String[] getModulesRequired() {
		return modulesRequired;
	}

	public void setModulesRequired(String... moduleIDs) {
		modulesRequired = moduleIDs;
	}
}
