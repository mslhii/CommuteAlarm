package kritikalerror.com.commutealarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
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
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

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
    protected String mCurLocation;
    protected String mTime;
    protected String mEventTime;
    protected String mHabit;

    public String mAlarmTimeString;

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    private PriorityQueue<PendingIntent> mIntentQueue;

    private final int TIMER_DELAY = 100;
    private final int TIMER_PERIOD = 60 * 60 * 30; // 30 minute intervals

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e("TRAFFICSERVICE", "TrafficUpdateService started!");

        Bundle b = intent.getExtras();
        mTime = b.getString("time");
        mHabit = b.getString("habit");
        mEventTime = b.getString("event");
        mLocation = b.getString("location");
        mCurLocation = b.getString("curloc");

        // We don't want a null instance
        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mHour = mCalendar.get(Calendar.HOUR);
        mMinute = mCalendar.get(Calendar.MINUTE);

        // Start Queue of alarms
        mIntentQueue = new PriorityQueue<PendingIntent>();

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // If there any previous alarms, remove them
        //mAlarmManager.cancel();

        //DEBUGGING ONLY!
        //new getTimeTask().execute(mLocation);

        //TODO: need to keep timer running until alarm rings, then stop it
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                new getTimeTask().execute(mLocation);
            }
        }, TIMER_DELAY, TIMER_PERIOD);

        Toast.makeText(TrafficUpdateService.this, "TrafficUpdateService finished loading!", Toast.LENGTH_LONG).show();
        Log.e("STOPTRAFFIC", "TrafficUpdateService finished loading!");

        return START_NOT_STICKY;
    }

    /**
     * Destroy the most recent alarm set and quit the service
     */
    @Override
    public void onDestroy()
    {
//        Intent myIntent = new Intent(TrafficUpdateService.this, AlarmReceiver.class);
//        mPendingIntent = PendingIntent.getBroadcast(TrafficUpdateService.this, AlarmSupport.ALARM_ID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        mPendingIntent.cancel();
//        mAlarmManager.cancel(mPendingIntent);
//
//        Intent stopAlarmIntent = new Intent(this, AlarmService.class);
//        stopService(stopAlarmIntent);

        Toast.makeText(TrafficUpdateService.this, "Stopping TrafficUpdateService!", Toast.LENGTH_LONG).show();
        Log.e("STOPTRAFFIC", "Stopping TrafficUpdateService!");
        destroyAllAlarms();
    }

    private void destroyAllAlarms()
    {
        for(PendingIntent pendingIntent : mIntentQueue)
        {
            pendingIntent.cancel();
            mAlarmManager.cancel(pendingIntent);

            Intent stopAlarmIntent = new Intent(this, AlarmService.class);
            stopService(stopAlarmIntent);
        }
    }

    private class getTimeTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // We don't want a null instance
            mCalendar = Calendar.getInstance();
            mYear = mCalendar.get(Calendar.YEAR);
            mMonth = mCalendar.get(Calendar.MONTH);
            mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            mHour = mCalendar.get(Calendar.HOUR);
            mMinute = mCalendar.get(Calendar.MINUTE);

            Log.d("TUService", "Starting asynctask!");
        }

        @Override
        protected String doInBackground(String... params) {
            String queryResult = "Cannot get result!";
            String locParams = "";
            try {
                locParams = mCurLocation;
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

            Intent myIntent = new Intent(TrafficUpdateService.this, AlarmReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(TrafficUpdateService.this, AlarmSupport.ALARM_ID, myIntent, 0);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), mPendingIntent);
            }
            else
            {
                mAlarmManager.set(AlarmManager.RTC, mCalendar.getTimeInMillis(), mPendingIntent);
            }

            Log.e("POST", "Calendar time is: " + mCalendar.getTime().toString());

            Toast.makeText(TrafficUpdateService.this, "Alarm has been set to: " + mAlarmTimeString, Toast.LENGTH_LONG).show();
            AlarmSupport.createNotification(getApplicationContext(), "Alarm has been set to: " + mAlarmTimeString);
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
                Toast.makeText(TrafficUpdateService.this, "Cannot parse time! Reason: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return returnTime;
        }
    }
}
