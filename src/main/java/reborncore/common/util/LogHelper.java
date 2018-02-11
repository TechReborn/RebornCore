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

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reborncore.common.IModInfo;

public class LogHelper {

	IModInfo modInfo;
	Logger logger;

	public LogHelper(IModInfo modInfo) {
		this.modInfo = modInfo;
		ModContainer modContainer = Loader.instance().getActiveModList()
			.stream().filter(modContainer1 -> modContainer1.getModId()
				.equals(modInfo.MOD_ID())).findFirst().get();
		logger = LogManager.getLogger(modContainer);
	}

	public void log(Level logLevel, Object object) {
		logger.log(logLevel, String.valueOf(object));
	}

	public void all(Object object) {
		log(Level.ALL, object);
	}

	public void debug(Object object) {
		log(Level.DEBUG, object);
	}

	public void error(Object object) {
		log(Level.ERROR, object);
	}

	public void fatal(Object object) {
		log(Level.FATAL, object);
	}

	public void info(Object object) {
		log(Level.INFO, object);
	}

	public void off(Object object) {
		log(Level.OFF, object);
	}

	public void trace(Object object) {
		log(Level.TRACE, object);
	}

	public void warn(Object object) {
		log(Level.WARN, object);
	}

}
