package kritikalerror.com.commutealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Michael on 4/9/2015.
 */
public class TrafficUpdateService extends Service {
    private Calendar mCalendar;
    private int mYear = 0;
    private int mDay = 0;
    private int mMonth = 0;
    private int mHour = 0;
    private int mMinute = 0;

    protected String mLocation;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Bundle b = intent.getExtras();
        b.getString("time");
        b.getString("habit");
        b.getString("event");
        mLocation = b.getString("location");

        // We don't want a null instance
        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mHour = mCalendar.get(Calendar.HOUR);
        mMinute = mCalendar.get(Calendar.MINUTE);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {

    }

    /*
    private class getTimeTask extends AsyncTask<String, Void, String> {
        private Calendar mCalendar;
        private int mYear = 0;
        private int mDay = 0;
        private int mMonth = 0;
        private int mHour = 0;
        private int mMinute = 0;

        @Override
        protected void onPreExecute() {
            // We don't want a null instance
            mCalendar = Calendar.getInstance();
            mYear = mCalendar.get(Calendar.YEAR);
            mMonth = mCalendar.get(Calendar.MONTH);
            mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            mHour = mCalendar.get(Calendar.HOUR);
            mMinute = mCalendar.get(Calendar.MINUTE);
        }

        @Override
        protected String doInBackground(String... params) {
            String queryResult = "Cannot get result!";
            String locParams = "";
            try {
                if(mLastLocation != null) {
                    locParams = String.valueOf(mLastLocation.getLatitude()) + "," +
                            String.valueOf(mLastLocation.getLongitude());
                }
                else
                {
                    //TODO: else use sharedprefs to get last location
                    locParams = "Palo Alto";
                }
                queryResult = AlarmSupport.queryGoogle(locParams, params[0]);
                mAlarmTimeString = queryResult;
            }
            catch (Exception e) {
                Log.e("EXCEPTION", e.getMessage());
            }
            return queryResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("TIME", s);

            mAlarmTimeString = subtractTime(s);

            if(mAlarmTimeString.equals("")) {
                mAlarmTimeString = "Cannot set time!";
            }

            // Add proper dates
            mCalendar.set(Calendar.YEAR, mYear);
            mCalendar.set(Calendar.MONTH, mMonth);
            // We need to determine the correct day to set the alarm to
            if(mHour > mCalendar.get(Calendar.HOUR)) {
                mCalendar.set(Calendar.DAY_OF_MONTH, mDay + 1);
            }
            else
            {
                mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
            }

            //TODO: if found alarm is less

            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(MainActivity.this, ALARM_ID, myIntent, 0);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);
            }
            else
            {
                mAlarmManager.set(AlarmManager.RTC, mCalendar.getTimeInMillis(), mPendingIntent);
            }

            Log.e("POST", "Calendar time is: " + mCalendar.getTime().toString());

            Toast.makeText(MainActivity.this, mAlarmTimeString, Toast.LENGTH_LONG).show();

            mAlarmTextView.setText("Alarm will be set to: \n" + mCalendar.getTime().toString());
        }

        private String subtractTime(String inTime) {
            SimpleDateFormat sdf  = new SimpleDateFormat("HH:mm");
            Date eventDate = AlarmSupport.convertStringToTime(mEventTime);
            mCalendar = AlarmSupport.dateToCalendar(eventDate);
            String returnTime = "";
            Log.e("TIMER", "Event time is: " + sdf.format(mCalendar.getTime()));

            try {
                if(inTime == null)
                {
                    throw new Exception("inTime is null!");
                }
                int seconds = Integer.parseInt(inTime);

                // Make seconds negative
                seconds = -seconds;
                mCalendar.add(Calendar.SECOND, seconds);
                returnTime = sdf.format(mCalendar.getTime());
                Log.e("TIMER", "Finished is: " + returnTime);
            }
            catch (Exception e) {
                Toast.makeText(MainActivity.this, "Cannot parse time! Reason: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return returnTime;
        }
    }
    */
}
