package rider.dev.asliborneo.app.myridebah.Commons;

import android.location.Location;

import rider.dev.asliborneo.app.myridebah.Remote.FCMService;

public class Commons {
    public static Location mLastLocation;
    public static final String driver_location="Drivers";
    public static final String Registered_driver="DriverInformation";
    public static final String Registered_Riders="RidersInformation";
    public static final String pickUpRequest_tbl="PickUpRequest";
    public static final String tokens_tbl="Tokens";
    private static final double Base_Fare=2.55;
    public static String destination;
    private static  final double Time_Rate=0.35;
    private static  final double Distance_Rate=1.75;
    public  static  Boolean isDriveravailable=false;
    public static String driver_id = "";
    public static String final_calculate;



    public static final java.lang.String user_field="usr";
    public static final java.lang.String password_field="pwd";
    public static double getPrice(double km,int min){
        return (Base_Fare+(Time_Rate*min)+(Distance_Rate*km));
    }




}
