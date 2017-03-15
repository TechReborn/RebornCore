package reborncore.dev;

import net.minecraft.item.Item;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by modmuss50 on 15/03/2017.
 */
public class EmptyItemStackChecker {

	public static File exportFile;

	private static String NEW_LINE = System.getProperty("line.separator");

	public static void setItem(Item newItem) {
		if(newItem == null){
			System.out.println("An itemstack was created with a null item!");
			StringBuilder sb = new StringBuilder();
			sb.append("An itemstack was created with a null item!");
			sb.append(NEW_LINE);
			sb.append(NEW_LINE);
			for(StackTraceElement line : Thread.currentThread().getStackTrace()){
				sb.append(line);
				sb.append(NEW_LINE);
			}
			for (int i = 0; i < 10; i++) {
				sb.append(NEW_LINE);
			}

			try {
				FileUtils.writeStringToFile(exportFile, sb.toString(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
