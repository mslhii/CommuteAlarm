package kritikalerror.com.commutealarm;

/**
 * Created by Michael on 8/16/2015.
 */
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.EditText;

public class SettingsActivity extends PreferenceActivity {

    private final String PREFS_NAME = "TaskRecorderPrefs";
    private final String CUR_LOC_KEY = "CurrentLocation";
    private final String LOCATION_KEY = "Location";
    private final String HABIT_KEY = "Habit";
    private final String EVENT_KEY = "Event";
    private final String TOGGLE_KEY = "Toggle";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load SharedPrefs to get default values
        SharedPreferences sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
        PreferenceCategory category = new PreferenceCategory(this);
        category.setTitle("User Settings");

        screen.addPreference(category);

        //CheckBoxPreference checkBoxPref = new CheckBoxPreference(this);
        //checkBoxPref.setTitle("title");
        //checkBoxPref.setSummary("summary");
        //checkBoxPref.setChecked(true);
        //category.addPreference(checkBoxPref);

        EditTextPreference locPref = new EditTextPreference(this);
        locPref.setTitle("Where do you work?");
        locPref.setSummary("Enter address of where you work");
        locPref.setKey(LOCATION_KEY);
        String location = sharedPrefs.getString(LOCATION_KEY, null);
        if(location != null) {
            locPref.setText(location);
        }
        else {
            locPref.setText("");
        }
        category.addPreference(locPref);

        EditTextPreference habitPref = new EditTextPreference(this);
        habitPref.setTitle("How long does it take to prepare to leave?");
        habitPref.setSummary("Enter time it takes to leave in minutes");
        habitPref.setKey(HABIT_KEY);
        String habit = sharedPrefs.getString(HABIT_KEY, null);
        if(habit != null) {
            habitPref.setText(habit);
        }
        else {
            habitPref.setText("");
        }
        category.addPreference(habitPref);

        EditTextPreference eventPref = new EditTextPreference(this);
        eventPref.setTitle("When do you need to be in the office?");
        eventPref.setSummary("Enter time of first event in morning");
        eventPref.setKey(EVENT_KEY);
        String event = sharedPrefs.getString(EVENT_KEY, null);
        if(event != null) {
            eventPref.setText(event);
        }
        else {
            eventPref.setText("");
        }
        category.addPreference(eventPref);

        //DialogPreference dialogPref = new DialogPreference(this);
        //dialogPref .setTitle("Reset App");
        //dialogPref .setSummary("Reset");
        //category.addPreference(dialogPref);

        setPreferenceScreen(screen);
        //addPreferencesFromResource(R.xml.settings);
        //PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }
}