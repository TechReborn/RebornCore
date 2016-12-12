package reborncore.common.util;

import reborncore.RebornCore;

import java.util.Calendar;

/**
 * Created by Mark on 27/11/2016.
 */
public class CalenderUtils {

	public static boolean christmas;

	public static void loadCalender(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1; //Java months start at 0
		if(month == 12){
			if(day >= 20 && day <= 30){
				christmas = true;
				RebornCore.logHelper.info("Merry christmas from reborn core! :)");
			}
		}


	}
}
