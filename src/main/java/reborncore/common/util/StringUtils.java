package reborncore.common.util;

/**
 * @author Prospector on 11/05/16
 */
public class StringUtils {

	public static String toFirstCapital(String input) {
		if (input == null || input.length() == 0) {
			return input;
		}

		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

}
