package kritikalerror.com.commutealarm;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Michael on 2/22/2015.
 */
public class AlarmSupport {

    public static String API_KEY = "AIzaSyAQJ0PLfQ0pBmXJ0TPkBHFbLYL-ATYtk-A";

    public AlarmSupport() {
        // Static class
    }

    public static String queryGoogle(String location)
    {
        String returnTime = "";
        String uriBuilder = "https://maps.googleapis.com/maps/api/directions/json?";

        //https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=AIzaSyAQJ0PLfQ0pBmXJ0TPkBHFbLYL-ATYtk-A

        return returnTime;
    }

    public static Date convertStringToTime(String time)
    {
        Date finalDate = new Date();

        return finalDate;
    }
}
