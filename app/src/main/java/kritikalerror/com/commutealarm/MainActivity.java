package kritikalerror.com.commutealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends Activity {

    private EditText mTimeBox;
    private EditText mLocationBox;
    private TextView mAlarmTextView;
    private Button mSubmitButton;
    private TextView mAlarmRingerView;
    private EditText mHabitBox;
    private EditText mEventBox;
    private ToggleButton mAlarmToggle;

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    private String mTime;
    private String mLocation;
    private String mHabit;
    private String mEventTime;

    protected String mAlarmTimeString;

    private final int ALARM_ID = 1248940;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAlarmTimeString = "Cannot set alarm!";

        mEventBox = (EditText) findViewById(R.id.firstEvent);
        mTimeBox = (EditText) findViewById(R.id.sleepEdit);
        mLocationBox = (EditText) findViewById(R.id.workEdit);
        mAlarmTextView = (TextView) findViewById(R.id.alarmNotification);
        mSubmitButton = (Button) findViewById(R.id.submit);
        mAlarmRingerView = (TextView) findViewById(R.id.ringer);
        mHabitBox = (EditText) findViewById(R.id.prepareTime);

        //DEBUG ONLY!
        mTimeBox.setText("23:00");
        mLocationBox.setText("1 Infinite Loop");
        mHabitBox.setText("30 minutes");
        mEventBox.setText("09:30");

        mTime = mTimeBox.getText().toString();
        mLocation = mLocationBox.getText().toString();
        mHabit = mHabitBox.getText().toString();
        mEventTime = mEventBox.getText().toString();

        mAlarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mSubmitButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(MainActivity.this, "Setting Alarm!", Toast.LENGTH_SHORT).show();

                mTime = mTimeBox.getText().toString();
                mHabit = mHabitBox.getText().toString();
                mEventTime = mEventBox.getText().toString();

                //TODO: make text boxes SharedPreferences
                mLocation = mLocationBox.getText().toString();
                new getTimeTask().execute(mLocation);
            }

        });
    }

    public void setAlarmText(String alarmText)
    {
        mAlarmRingerView.setText(alarmText);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        else
        {
            mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(MainActivity.this, ALARM_ID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mPendingIntent.cancel();
            mAlarmManager.cancel(mPendingIntent);

            Intent stopIntent = new Intent(this, AlarmService.class);
            stopService(stopIntent);

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

        @Override
        protected String doInBackground(String... params) {
            String queryResult = "Cannot get result!";
            try {
                queryResult = AlarmSupport.queryGoogle("Palo Alto", params[0]);
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

            Toast.makeText(MainActivity.this, mAlarmTimeString, Toast.LENGTH_LONG).show();
        }

        private String subtractTime(String inTime) {
            SimpleDateFormat sdf  = new SimpleDateFormat("HH:mm");
            String returnTime = "";
            try {
                long seconds = Long.parseLong(inTime);
                Date eventDate = sdf.parse(mEventTime);
                //long timeInMillisSinceEpoch = inDate.getTime();
                //long timeInMinutesSinceEpoch = timeInMillisSinceEpoch / (60 * 1000);
                Log.e("TIME", Long.toString(eventDate.getTime()));
                Log.e("TIME", Long.toString(seconds));
                Date currentDate = new Date(eventDate.getTime() - seconds);
                returnTime = sdf.format(currentDate);
            }
            catch (Exception e) {
                Toast.makeText(MainActivity.this, "Cannot parse time! Reason: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return returnTime;
        }
    }
}