package rider.dev.asliborneo.app.myridebah.Commons;

import android.location.Location;

import rider.dev.asliborneo.app.myridebah.Remote.FCMService;
import rider.dev.asliborneo.app.myridebah.Remote.GoogleMAPApi;
import rider.dev.asliborneo.app.myridebah.Remote.IGoogleApi;
import rider.dev.asliborneo.app.myridebah.Remote.RetrofitClient;

public class Commons {

    public static final String driver_location = "Drivers";
    public static final String Registered_driver = "DriverInformation";
    public static final String Registered_Riders = "RidersInformation";
    public static final String pickUpRequest_tbl = "PickUpRequest";
    public static final String tokens_tbl = "Tokens";
    public static Location mLastLocation;

    private static final double base_fare = 2.55;
    private static final double time_rate = 0.35;
    private static final double distance_rate = 1.75;
    public static final String fcmURL = "https://fcm.googleapis.com/";
    public static final String googleApiURL = "https://maps.googleapis.com";

    public static final java.lang.String user_field = "usr";
    public static final java.lang.String password_field = "pwd";
    public static boolean isDriveravailable;
    public static String driver_id;


    public static FCMService getFCMService() {
        return RetrofitClient.getClient().create(FCMService.class);
    }

    public static double getPrice(double km,int min){
        return (base_fare+(time_rate*min)+(distance_rate*km));
    }

    public static IGoogleApi getGoogleService()
    {
        return GoogleMAPApi.getClient(googleApiURL).create(IGoogleApi.class);
    }
}





