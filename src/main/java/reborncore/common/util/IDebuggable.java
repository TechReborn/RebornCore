package reborncore.common.util;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface IDebuggable {

	String getDebugText();


	// Formatting helpers
	static String propertyFormat(String property, String info){
		String s = "" + Formatting.GREEN;
		s += property + ": ";
		s += Formatting.BLUE;
		s += info;

		return s;
	}
}
