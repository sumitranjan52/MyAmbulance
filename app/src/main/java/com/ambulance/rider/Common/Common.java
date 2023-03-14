package com.ambulance.rider.Common;

/**
 * Created by sumit on 19-Feb-18.
 */

public class Common {

    public static double latitude;
    public static double longitude;

    public static boolean isLoggedIn = false;

    public static final String LOG_TAG = "RIDER";

    //public static final String BASE_URL = "http://hiva26.com/ambulanceapi/";
    public static final String BASE_URL = "http://ec2-43-204-238-64.ap-south-1.compute.amazonaws.com/ambulanceapi/";
    public static final String RIDER_URL = "rider.php";
    public static final String TOKEN_URL = "fcm.php";
    public static final String LOCATION_URL = "driverLocation.php";
    public static final String REQUEST_URL = "rideRequest.php";
    public static final String FARE_URL = "fare.php";

    public static final String API_KEY = "bc89d235143bd60073d0c41655328993";

    public static boolean isConnected = false;

    /* Requester details summary */
    public static String emergencyRequestBookingID = null;
    public static boolean isBooked = false;
    public static boolean isBookingAccepted = false;
    public static String driverPhone = "";
    public static String driverName = "";
    public static String ambulanceNo = "";
}
