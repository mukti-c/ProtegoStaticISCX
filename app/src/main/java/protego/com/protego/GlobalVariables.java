package protego.com.protego;

import java.util.Date;

public class GlobalVariables {
    public static Date startTime;
    public static Date endTime;
    public static String connSourceIP = null;
    public static String connDestIP = null;
    public static int connSourcePort = 0;
    public static int connDestPort = 0;
    public static String connProtocol = null;
    public static String connService = null;
    public static String chosen_Dir="";

    public static void clearVar() {
        startTime = null;
        endTime = null;
        connSourceIP = null;
        connSourcePort = 0;
        connDestIP = null;
        connDestPort = 0;
        connProtocol = null;
        connService = null;
    }
}
