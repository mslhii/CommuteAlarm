package kritikalerror.com.commutealarm;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String queryGoogle(String location, String destination)
    {
        String returnTime = "";
        String uriBuilder = "https://maps.googleapis.com/maps/api/directions/json?";

        // Replace spaces with more URI friendly characters
        location = location.replaceAll(" ","%20");
        destination = destination.replaceAll(" ","%20");

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
            Log.e("JSON", responseString);
            JSONArray response = new JSONObject(responseString).getJSONArray("routes");
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
        SimpleDateFormat sdf  = new SimpleDateFormat("HH:mm");
        Date finalDate = null;
        try {
            finalDate = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    @SuppressWarnings("resource")
    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static String parseJSON(JSONArray response) {
        String totalTime = "Cannot be parsed";
        JSONArray legs;
        try {
            legs = response.getJSONObject(0).getJSONArray("legs");
            Log.e("PARSE", legs.toString());
            JSONObject duration = (JSONObject) legs.getJSONObject(0).getJSONObject("duration");
            Log.e("PARSE", duration.toString());
            totalTime = duration.getString("value");
            Log.e("PARSE", totalTime);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return totalTime;
    }

    public static Calendar dateToCalendar(Date date){
        Log.e("DATE", date.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
