package protego.com.protego;

import java.util.Calendar;

public class GetTime {

    public static String getCurrentTime()
    {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec=c.get(Calendar.SECOND);
        int ds = c.get(Calendar.AM_PM);

        String time = Integer.toString(hour)+":"+Integer.toString(min)+":"+Integer.toString(sec);
        return time;
    }
}
