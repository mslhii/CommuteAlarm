package kritikalerror.com.commutealarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Typeface;

/**
 * Created by Michael on 6/22/2015.
 */
public class CustomPagerAdapter extends PagerAdapter {

    public final int numberOfPages = 6;

    private Context mContext;
    public EditText mUserAnswerView;

    private String mTimeSelected = "";

    private int[] backgroundColor = {
            0xFF101010,
            0xFF202020,
            0xFF303030,
            0xFF404040,
            0xFF505050,
            0xFF606060,
    };

    private String[] questionStrings = {
            "Hello",
            "Before you use this app, let me ask you a few questions",
            "Where do you work?",
            "How long does it take to prepare to leave?",
            "When do you need to be in the office?",
            "Click the screen to save settings and continue!"
    };

    public CustomPagerAdapter(Context context) {
        mContext = context;
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
        textView.setGravity(Gravity.CENTER);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        layout.setBackgroundColor(backgroundColor[position]);
        layout.setLayoutParams(layoutParams);
        layout.addView(textView);

        // This is for all the non-TextView pages
        if(!(position == (numberOfPages - 1)) && (position > 1))
        {
            mUserAnswerView = new EditText(mContext);
            mUserAnswerView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            mUserAnswerView.setTag("textview");
            layout.addView(mUserAnswerView);
        }

        // TimePicker dialog
        if(position == (numberOfPages - 2))
        {
            TimePicker timePickerView = new TimePicker(mContext);
            timePickerView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            timePickerView.setTag("timepickerview");
            mUserAnswerView.setVisibility(View.INVISIBLE);
            layout.addView(timePickerView);

            timePickerView.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        @Override
                        public void onTimeChanged(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            StringBuilder sb = new StringBuilder().append((selectedHour)).append(":").append((selectedMinute));
                            Toast.makeText(mContext, "Time changed to " + selectedHour + ":" + selectedMinute + "!", Toast.LENGTH_SHORT).show();
                            mTimeSelected = sb.toString();
                            mUserAnswerView.setText("New time is: " + mTimeSelected); //bug
                        }
                    });
        }

        final Context lastContext = mContext;
        final int lastPosition = position;
        layout.setOnClickListener(new OnClickListener(){
        @Override
        public void onClick(View v) {
            if (lastPosition == (numberOfPages - 1)) {
                Intent setActivityIntent = new Intent(lastContext, AlarmSetActivity.class);
                setActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                lastContext.startActivity(setActivityIntent);
            }
        }});

        container.addView(layout);
        layout.setTag("pos" + position);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
        //if(position == (numberOfPages - 2)) {
        //    Toast.makeText(mContext, "Timepicker destroyed!", Toast.LENGTH_SHORT).show();
        //}
    }

}
