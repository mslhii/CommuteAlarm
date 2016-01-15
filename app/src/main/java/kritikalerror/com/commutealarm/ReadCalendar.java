package kritikalerror.com.commutealarm;

/**
 * Created by Michael on 9/23/2015.
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

// forgot why i need this, its been a while...
public class ReadCalendar {
    public static ArrayList<String> nameOfEvent = new ArrayList<String>();
    public static ArrayList<String> startDates = new ArrayList<String>();
    public static ArrayList<String> endDates = new ArrayList<String>();
    public static ArrayList<String> descriptions = new ArrayList<String>();
    private static int calendarId = 0;
    final protected static String CALENDAR_URI = "content://com.android.calendar/events";
    /**
     * Function that gets all instances of YES! for all time
     * @param context
     * @return
     */
    public static ArrayList<String> readCalendarEvent(Context context) {
        // If event name has "YES!" in it, it's ours
        String selection = "title LIKE ?";
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse(CALENDAR_URI),
                        new String[] { "calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation" }, selection, new String[]{"YES!%"}, null);
        cursor.moveToFirst();
        // Get calendar name
        String cNames[] = new String[cursor.getCount()];
        // Get calendar ID
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        for (int i = 0; i < cNames.length; i++) {
            calendarId = cursor.getInt(0);
            nameOfEvent.add(cursor.getString(1));
            startDates.add(getDate(Long.parseLong(cursor.getString(3))));
            endDates.add(getDate(Long.parseLong(cursor.getString(4))));
            descriptions.add(cursor.getString(2));
            cNames[i] = cursor.getString(1);
            cursor.moveToNext();
        }
        return nameOfEvent;
    }
    /**
     * Overloaded function that gets number of events from a start to end date
     * @param context
     * @param startDate
     * @param endDate
     * @return
     */
    public static ArrayList<String> readCalendarEvent(Context context, long startDate, long endDate) {
        // If event name has "YES!" in it, it's ours
        //String selection = "title LIKE ? AND dtstart > ? AND dtend < ?";
        String selection = "title LIKE ?";
        String start = String.valueOf(startDate);
        //start = "08/04/2015 01:50:00 AM";
        String end = String.valueOf(endDate);

        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse(CALENDAR_URI),
                        new String[] { "calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation" }, selection, new String[]{"YES!%"}, null);
        cursor.moveToFirst();
        // Get calendar name
        String cNames[] = new String[cursor.getCount()];
        // Get calendar ID
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        for (int i = 0; i < cNames.length; i++) {
            if(Long.parseLong(cursor.getString(3)) > startDate && Long.parseLong(cursor.getString(3)) < endDate) {
                calendarId = cursor.getInt(0);
                nameOfEvent.add(cursor.getString(1));
                startDates.add(getDate(Long.parseLong(cursor.getString(3))));
                endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                descriptions.add(cursor.getString(2));
                cNames[i] = cursor.getString(1);
                // DEBUG to check dates
                Log.e("QUERYDATE", cursor.getString(3));
                Log.e("STARTDATE", String.valueOf(startDate));
                Log.e("ENDDATE", String.valueOf(endDate));
            }
            cursor.moveToNext();
        }
        return nameOfEvent;
    }
    public static void deleteAllEvents(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor;
        String selection = "calendar_id=" + calendarId + " AND title LIKE ?";
        cursor = resolver.query(Uri.parse(CALENDAR_URI), new String[]{ "_id" }, selection, new String[]{"YES!%"}, null);
        while(cursor.moveToNext()) {
            long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
            resolver.delete(ContentUris.withAppendedId(Uri.parse(CALENDAR_URI), eventId), null, null);
        }
        cursor.close();
    }
    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    public static long getLongDate(String time) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Date d;
        try {
            d = f.parse(time);
            long milliseconds = d.getTime();
            return milliseconds;
        } catch (ParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
}
