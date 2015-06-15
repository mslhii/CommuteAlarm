package kritikalerror.com.commutealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Michael on 6/13/2015.
 */
public class AlarmSetActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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

    private String mLocation;
    private String mHabit;
    private String mEventTime;

    protected String mAlarmTimeString;
    protected Location mLastLocation;
    private boolean mIsToggleChecked = false;

    private final String PREFS_NAME = "TaskRecorderPrefs";
    private final String CUR_LOC_KEY = "CurrentLocation";
    private final String LOCATION_KEY = "Location";
    private final String HABIT_KEY = "Habit";
    private final String EVENT_KEY = "Event";
    private final String TOGGLE_KEY = "Toggle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        // Start up Google client
        buildGoogleApiClient();

        // Start SharedPreferences
        mPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mEditor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        mAlarmTimeString = "Cannot set alarm!";

        mEventBox = (EditText) findViewById(R.id.firstEvent);
        mLocationBox = (EditText) findViewById(R.id.workEdit);
        mAlarmTextView = (TextView) findViewById(R.id.alarmNotification);
        mAlarmRingerView = (TextView) findViewById(R.id.ringer);
        mHabitBox = (EditText) findViewById(R.id.prepareTime);
        mSaveButton = (Button) findViewById(R.id.button);

        // Get values from SharedPrefs
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
        mIsToggleChecked = mPreferences.getBoolean(TOGGLE_KEY, false);

        mLocation = mLocationBox.getText().toString();
        mHabit = mHabitBox.getText().toString();
        mEventTime = mEventBox.getText().toString();

        mAlarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mAlarmToggle.setChecked(mIsToggleChecked);

        mSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mEditor.putString(LOCATION_KEY, mLocationBox.getText().toString());
                mEditor.putString(HABIT_KEY, mHabitBox.getText().toString());
                mEditor.putString(EVENT_KEY, mEventBox.getText().toString());
                mEditor.putString(CUR_LOC_KEY, Double.toString(mLastLocation.getLatitude()) +
                        "," + Double.toString(mLastLocation.getLongitude()));
                mEditor.putBoolean(TOGGLE_KEY, mAlarmToggle.isChecked());
                mEditor.commit();
            }
        });
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

            Toast.makeText(AlarmSetActivity.this, "Setting Alarm!", Toast.LENGTH_SHORT).show();

            mHabit = mHabitBox.getText().toString();
            mEventTime = mEventBox.getText().toString();

            mLocation = mLocationBox.getText().toString();

            //new getTimeTask().execute(mLocation);

            // Put current location in string
            String locParams = "";
            if(mLastLocation != null) {
                locParams = String.valueOf(mLastLocation.getLatitude()) + "," +
                        String.valueOf(mLastLocation.getLongitude());
            }
            else
            {
                locParams = mPreferences.getString(CUR_LOC_KEY, null);
            }

            Toast.makeText(AlarmSetActivity.this, "Starting TrafficUpdateService!", Toast.LENGTH_LONG).show();
            Log.e("STARTTRAFFIC", "Starting TrafficUpdateService!");
            Intent updateServiceIntent = new Intent(this, TrafficUpdateService.class);
            Bundle sendBundle = new Bundle();
            sendBundle.putString("habit", mHabit);
            sendBundle.putString("event", mEventTime);
            sendBundle.putString("location", mLocation);
            sendBundle.putString("curloc", locParams);
            updateServiceIntent.putExtras(sendBundle);
            startService(updateServiceIntent);
        }
        else
        {
            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(AlarmSetActivity.this, AlarmReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(AlarmSetActivity.this, AlarmSupport.ALARM_ID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
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

        // Update the SharedPrefs too
        mEditor.putBoolean(TOGGLE_KEY, mAlarmToggle.isChecked());
        mEditor.commit();
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
}
