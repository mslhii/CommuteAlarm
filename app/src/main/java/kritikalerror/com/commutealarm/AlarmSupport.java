package kritikalerror.com.commutealarm;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Michael on 2/22/2015.
 */
public class AlarmSupport {

    public static String API_KEY = "AIzaSyAQJ0PLfQ0pBmXJ0TPkBHFbLYL-ATYtk-A";

    public AlarmSupport() {
        // Static class
    }

    public static String queryGoogle(String location, String destination)
    {
        String returnTime = "";
        String uriBuilder = "https://maps.googleapis.com/maps/api/directions/json?";

        //https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=AIzaSyAQJ0PLfQ0pBmXJ0TPkBHFbLYL-ATYtk-A
        uriBuilder = uriBuilder + "origin=" + location +
                "&destination=" + destination +
                "&key=" + API_KEY;

        Log.e("REQUEST", uriBuilder);

        try {
            // Prepare the URL
            URL url = new URL(uriBuilder);
            // Prepare the connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Connect and get results
            conn.connect();
            String responseString = convertStreamToString(conn.getInputStream());
            JSONObject response = new JSONObject(responseString);
            returnTime = parseJSON(response);
            conn.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return returnTime;
    }

    public static Date convertStringToTime(String time)
    {
        Date finalDate = new Date();

        return finalDate;
    }

    @SuppressWarnings("resource")
    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static String parseJSON(JSONObject response) {
        String totalTime = "";
        JSONObject geometry;
        try {
            geometry = (JSONObject) response.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            //this.setLatitude(location.getDouble("lat"));
            //this.setLongitude(location.getDouble("lng"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return totalTime;
    }
}
