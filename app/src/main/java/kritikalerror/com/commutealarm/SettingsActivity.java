package kritikalerror.com.commutealarm;

/**
 * Created by Michael on 8/16/2015.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        //PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }
}