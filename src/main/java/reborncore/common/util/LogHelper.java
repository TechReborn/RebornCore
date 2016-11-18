package reborncore.common.util;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;
import reborncore.common.IModInfo;

public class LogHelper {

	IModInfo modInfo;

	public LogHelper(IModInfo modInfo) {
		this.modInfo = modInfo;
	}

	public void log(Level logLevel, Object object) {
		FMLLog.log(modInfo.MOD_NAME(), logLevel, String.valueOf(object));
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
