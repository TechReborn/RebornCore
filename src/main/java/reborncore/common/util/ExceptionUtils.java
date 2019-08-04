package reborncore.common.util;

public class ExceptionUtils {

	public static void tryAndThrow(Runnable runnable, String message) throws RuntimeException {
		try {
			runnable.run();
		} catch (Throwable t){
			throw new RuntimeException(message, t);
		}
	}

}
