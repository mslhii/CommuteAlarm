package kritikalerror.com.commutealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private EditText mTimeBox;
    private EditText mLocationBox;
    private TextView mAlarmTextView;
    private Button mSubmitButton;
    private TextView mAlarmRingerView;
    private EditText mHabitBox;

    private AlarmManager mAlarmManager;
    private static MainActivity inst;

    private String mTime;
    private String mLocation;
    private String mHabit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimeBox = (EditText) findViewById(R.id.sleepEdit);
        mLocationBox = (EditText) findViewById(R.id.workEdit);
        mAlarmTextView = (TextView) findViewById(R.id.alarmNotification);
        mSubmitButton = (Button) findViewById(R.id.submit);
        mAlarmRingerView = (TextView) findViewById(R.id.ringer);
        mHabitBox = (EditText) findViewById(R.id.prepareTime);

        mTime = mTimeBox.getText().toString();
        mLocation = mLocationBox.getText().toString();
        mHabit = mHabitBox.getText().toString();

        mSubmitButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(MainActivity.this, "Setting Alarm!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public static MainActivity instance() {
        return inst;
    }

    public void setAlarmText(String alarmText) {
        mAlarmRingerView.setText(alarmText.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
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
