package kritikalerror.com.commutealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private EditText mTimeBox;
    private EditText mLocationBox;
    private TextView mAlarmTextView;
    private TextView mAlarmRingerView;
    private EditText mHabitBox;
    private EditText mEventBox;
    private ToggleButton mAlarmToggle;
    private Button mSaveButton;

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private String mTime;
    private String mLocation;
    private String mHabit;
    private String mEventTime;

    protected String mAlarmTimeString;
    protected Location mLastLocation;

    private final int ALARM_ID = 1248940;
    private final String PREFS_NAME = "TaskRecorderPrefs";
    private final String TIME_KEY = "Time";
    private final String LOCATION_KEY = "Location";
    private final String HABIT_KEY = "Habit";
    private final String EVENT_KEY = "Event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start up Google client
        buildGoogleApiClient();

        // Start SharedPreferences
        mPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mEditor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        mAlarmTimeString = "Cannot set alarm!";

        mEventBox = (EditText) findViewById(R.id.firstEvent);
        mTimeBox = (EditText) findViewById(R.id.sleepEdit);
        mLocationBox = (EditText) findViewById(R.id.workEdit);
        mAlarmTextView = (TextView) findViewById(R.id.alarmNotification);
        mAlarmRingerView = (TextView) findViewById(R.id.ringer);
        mHabitBox = (EditText) findViewById(R.id.prepareTime);
        mSaveButton = (Button) findViewById(R.id.button);

        //DEBUG ONLY!
        //mTimeBox.setText("23:00");
        //mLocationBox.setText("1 Infinite Loop");
        //mHabitBox.setText("30 minutes");
        //mEventBox.setText("09:30");

        // Get values from SharedPrefs
        String time = mPreferences.getString(TIME_KEY, null);
        if(time != null) {
            mTimeBox.setText(time);
        }
        else {
            mTimeBox.setText("");
        }
        String location = mPreferences.getString(LOCATION_KEY, null);
        if(location != null) {
            mLocationBox.setText(location);
        }
        else {
            mLocationBox.setText("");
        }
        String habit = mPreferences.getString(HABIT_KEY, null);
        if(habit != null) {
            mHabitBox.setText(habit);
        }
        else {
            mHabitBox.setText("");
        }
        String event = mPreferences.getString(EVENT_KEY, null);
        if(event != null) {
            mEventBox.setText(event);
        }
        else {
            mEventBox.setText("");
        }

        mTime = mTimeBox.getText().toString();
        mLocation = mLocationBox.getText().toString();
        mHabit = mHabitBox.getText().toString();
        mEventTime = mEventBox.getText().toString();

        mAlarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    public void setAlarmText(String alarmText)
    {
        mAlarmRingerView.setText(alarmText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        ringtoneManager.stopPreviousRingtone();
    }

    public void onToggleClicked(View view) {
        if (mAlarmToggle.isChecked()) {
            Log.d("MyActivity", "Alarm On");

            Toast.makeText(MainActivity.this, "Setting Alarm!", Toast.LENGTH_SHORT).show();

            mTime = mTimeBox.getText().toString();
            mHabit = mHabitBox.getText().toString();
            mEventTime = mEventBox.getText().toString();

            // Do a 12/24 hr check here
            if(mTime.contains("am") && mTime.contains("12:"))
            {
                mTime = mTime.replace("am", "");
                mTime = mTime.replace("12:", "00:");
            }

            //TODO: make text boxes SharedPreferences
            mLocation = mLocationBox.getText().toString();
            //new getTimeTask().execute(mLocation);

            Intent updateServiceIntent = new Intent(this, TrafficUpdateService.class);
            Bundle sendBundle = new Bundle();
            sendBundle.putString("time", mTime);
            sendBundle.putString("habit", mHabit);
            sendBundle.putString("event", mEventTime);
            sendBundle.putString("location", mLocation);
            updateServiceIntent.putExtras(sendBundle);
            startService(updateServiceIntent);
        }
        else
        {
            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(MainActivity.this, ALARM_ID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mPendingIntent.cancel();
            mAlarmManager.cancel(mPendingIntent);

            Intent stopAlarmIntent = new Intent(this, AlarmService.class);
            stopService(stopAlarmIntent);

            Intent stopUpdateIntent = new Intent(this, TrafficUpdateService.class);
            stopService(stopUpdateIntent);

            mAlarmTextView.setText("Alarm will be set to: \nOff");
            setAlarmText("Alarm Off");
            Log.d("MyActivity", "Alarm Off");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

    /*
    Location related classes
     */
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            Toast.makeText(this, "Cannot find location!", Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.e("LOCFOUNDLAT", Double.toString(mLastLocation.getLatitude()));
            Log.e("LOCFOUNDLONG", Double.toString(mLastLocation.getLongitude()));
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("LOC", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("LOC", "Connection suspended");
        mGoogleApiClient.connect();
    }

    /*
    Debug ONLY!
     */
    /*
    private void _setAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE) + 1));
        mAlarmTextView.setText("Alarm will be set to: \n" +
                calendar.get(Calendar.HOUR_OF_DAY) +
                ":" +
                (calendar.get(Calendar.MINUTE) + 1));
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(MainActivity.this, ALARM_ID, myIntent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mPendingIntent);
        }
        else
        {
            mAlarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), mPendingIntent);
        }
    }
    */
}