package reborncore.common.powerSystem;


import reborncore.common.RebornCoreConfig;

public class PowerSystem {

    public static boolean RFPOWENET = RebornCoreConfig.enableRF;

    public static boolean EUPOWENET = RebornCoreConfig.enableEU;

    public static int euPerRF = RebornCoreConfig.euPerRF;


    public static String getLocaliszedPower(double eu){
        return getLocaliszedPower(eu);
    }

    public static String getLocaliszedPower(int eu){
        if(EUPOWENET){
            return getRoundedString(eu, "EU");
        } else {
            return getRoundedString(eu / euPerRF , "RF");
        }
    }


    private static String getRoundedString(double euValue, String units) {
        if (euValue >= 1000000) {
            double tenX = Math.round(euValue / 100000);
            return Double.toString(tenX / 10.0).concat(" m " + units);
        } else if (euValue >= 1000) {
            double tenX = Math.round(euValue / 100);
            return Double.toString(tenX / 10.0).concat(" k " + units);
        } else {
            return Double.toString(Math.floor(euValue)).concat(" " + units);
        }
    }

}
