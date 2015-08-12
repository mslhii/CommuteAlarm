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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
            SETUP_KEY,
    };

    ViewPager mViewPager;
    CustomPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide Action Bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
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
        mPagerAdapter = new CustomPagerAdapter(getApplicationContext());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int oldPos = mViewPager.getCurrentItem();

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageSelected(int pos) {
                //Toast.makeText(getApplicationContext(), "Current page " + pos, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "old page " + oldPos, Toast.LENGTH_SHORT).show();

                if(!(oldPos == (mPagerAdapter.numberOfPages - 1))) {
                    LinearLayout layout = (LinearLayout) mViewPager.findViewWithTag("pos" + oldPos);
                    EditText tempTextView = (EditText) layout.findViewWithTag("textview");

                    Toast.makeText(MainActivity.this, "Text is " + tempTextView.getText().toString() + "!", Toast.LENGTH_SHORT).show();

                    // Make commit to SharedPrefs
                    switch (oldPos) {
                        case 0:
                            mEditor.putString(prefStrings[0], tempTextView.getText().toString());
                            break;
                        case 1:
                            mEditor.putString(prefStrings[1], tempTextView.getText().toString());
                            break;
                        case 2:
                            mEditor.putString(prefStrings[2], tempTextView.getText().toString());
                            break;
                        default:
                            break;
                    }
                    mEditor.commit();
                }

                //Toast.makeText(MainActivity.this, "Finished committing case " + oldPos + "!", Toast.LENGTH_SHORT).show();

                oldPos = pos;
            }

        });
        mViewPager.setAdapter(mPagerAdapter);

        /*
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
                mEditor.putBoolean(SETUP_KEY, true);
                mEditor.commit();

                Intent setActivityIntent = new Intent(getApplicationContext(), AlarmSetActivity.class);
                setActivityIntent.setFlags(setActivityIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(setActivityIntent);

                Toast.makeText(MainActivity.this, "Finished committing! Starting AlarmSetActivity!", Toast.LENGTH_LONG).show();
            }
        });
        */
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