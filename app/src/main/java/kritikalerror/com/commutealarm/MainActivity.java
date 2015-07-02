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
import android.support.v4.view.ViewPager;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.PriorityQueue;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity {

    private EditText mLocationBox;
    private TextView mAlarmTextView;
    private TextView mAlarmRingerView;
    private EditText mHabitBox;
    private EditText mEventBox;
    private Button mSaveButton;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private String mHabit;
    private String mEventTime;

    protected String mAlarmTimeString;
    protected Location mLastLocation;

    private final String LOCATION_KEY = "Location";
    private final String HABIT_KEY = "Habit";
    private final String EVENT_KEY = "Event";
    private final String SETUP_KEY = "Setup";

    public String[] prefStrings = {
            LOCATION_KEY,
            HABIT_KEY,
            EVENT_KEY,
    };

    ViewPager mViewPager;
    CustomPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start SharedPreferences
        mPreferences = getSharedPreferences(AlarmSupport.PREFS_NAME, MODE_PRIVATE);
        mEditor = getSharedPreferences(AlarmSupport.PREFS_NAME, MODE_PRIVATE).edit();

        // See if we need to setup first
        boolean setup = mPreferences.getBoolean(SETUP_KEY, false);
        if(setup)
        {
            Toast.makeText(MainActivity.this, "We already have data! Starting AlarmSetActivity!", Toast.LENGTH_LONG).show();
            Intent setActivityIntent = new Intent(getApplicationContext(), AlarmSetActivity.class);
            setActivityIntent.setFlags(setActivityIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(setActivityIntent);
        }

        // Set up ViewPager
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int oldPos = mViewPager.getCurrentItem();

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
                //Toast.makeText(getApplicationContext(), "scrollstatechanged " + arg0, Toast.LENGTH_SHORT).show();

                //if(arg0 == ViewPager.SCROLL_STATE_SETTLING) { // Commit as soon as the page turns
                    // Make a commit every time the user flips a page
                    //Toast.makeText(getApplicationContext(), "Current page " + mViewPager.getCurrentItem(), Toast.LENGTH_SHORT).show();
                    //mEditor.putString(prefStrings[position], "");
                    //mEditor.commit();
                //}
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //int curPos = mViewPager.getCurrentItem();
                //Toast.makeText(getApplicationContext(), "arg1 " + arg1, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "arg2 " + arg2, Toast.LENGTH_SHORT).show();

                // TODO Auto-generated method stub
//                if(arg0 > curPos) {
//                    //Moving to the right
//
//                } else if(arg0 < curPos) {
//                    //Moving to the Left
//                }
            }

            @Override
            public void onPageSelected(int pos) {
                Toast.makeText(getApplicationContext(), "Current page " + pos, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "old page " + oldPos, Toast.LENGTH_SHORT).show();

                // Make commit to SharedPrefs
                switch(oldPos) {
                    case 0:
                        mEditor.putString(prefStrings[0], mLocationBox.getText().toString());
                        break;
                    case 1:
                        mEditor.putString(prefStrings[1], mHabitBox.getText().toString());
                        break;
                    case 2:
                        mEditor.putString(prefStrings[2], mEventBox.getText().toString());
                        break;
                    default:
                        break;
                }
                mEditor.commit();

                oldPos = pos;
            }

        });
        mPagerAdapter = new CustomPagerAdapter(getApplicationContext(), mPreferences, mEditor);
        mViewPager.setAdapter(mPagerAdapter);

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

        mHabit = mHabitBox.getText().toString();
        mEventTime = mEventBox.getText().toString();

        mSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mEditor.putString(LOCATION_KEY, mLocationBox.getText().toString());
                mEditor.putString(HABIT_KEY, mHabitBox.getText().toString());
                mEditor.putString(EVENT_KEY, mEventBox.getText().toString());
                mEditor.putBoolean(SETUP_KEY, true);
                mEditor.commit();

                Intent setActivityIntent = new Intent(getApplicationContext(), AlarmSetActivity.class);
                setActivityIntent.setFlags(setActivityIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(setActivityIntent);

                Toast.makeText(MainActivity.this, "Finished committing! Starting AlarmSetActivity!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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
}