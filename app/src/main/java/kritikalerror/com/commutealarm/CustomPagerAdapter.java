package kritikalerror.com.commutealarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Typeface;

/**
 * Created by Michael on 6/22/2015.
 */
public class CustomPagerAdapter extends PagerAdapter {

    private final int numberOfPages = 3;
    private final String LOCATION_KEY = "Location";
    private final String HABIT_KEY = "Habit";
    private final String EVENT_KEY = "Event";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    Context mContext;

    int[] res = {
            android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_compass,
    };

    int[] backgroundcolor = {
            0xFF101010,
            0xFF202020,
            0xFF303030,
    };

    String[] questionStrings = {
            "Where do you work?",
            "How long does it take to prepare to leave?",
            "When do you need to be in the office?",
    };

    String[] prefStrings = {
            LOCATION_KEY,
            HABIT_KEY,
            EVENT_KEY,
    };

    public CustomPagerAdapter(Context context, SharedPreferences sharedPrefs, SharedPreferences.Editor editor) {
        mContext = context;
        mPreferences = sharedPrefs;
        mEditor = editor;
    }

    @Override
    public int getCount() {
        return numberOfPages;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView textView = new TextView(mContext);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(30);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText(questionStrings[position]);

        EditText userAnswerView = new EditText(mContext);
        userAnswerView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(res[position]);
        LayoutParams imageParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        layout.setBackgroundColor(backgroundcolor[position]);
        layout.setLayoutParams(layoutParams);
        layout.addView(textView);
        //layout.addView(imageView);
        layout.addView(userAnswerView);

        final int page = position;
        final Context lastContext = mContext;
        layout.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(lastContext,
                        "Page " + page + " clicked",
                        Toast.LENGTH_LONG).show();
            }});

        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Make a commit every time the user flips a page
        //mEditor.putString(prefStrings[position], "");
        //mEditor.commit();
        Toast.makeText(mContext, "Changes committed to page " + position, Toast.LENGTH_SHORT).show();
        container.removeView((LinearLayout)object);
    }

}
