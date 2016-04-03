package kritikalerror.com.commutealarm;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Michael on 6/22/2015.
 */
public class CustomPagerAdapter extends PagerAdapter {

    public final int numberOfPages = 6;

    private Context mContext;
    private Activity mActivity;
    public EditText mUserAnswerView;

    private ArrayList<String> mTimeList;

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

    public CustomPagerAdapter(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        mTimeList = new ArrayList<String>();
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
            Button timeDialog = new Button(mContext);
            timeDialog.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            timeDialog.setText("Set Default Time");
            timeDialog.setTag("timepicker");
            timeDialog.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Process to get Current Time
                    final Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog tpd = new TimePickerDialog(mActivity,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int selectedHour,
                                                      int selectedMinute) {
                                    // Display Selected time in textbox
                                    String finalTime = pad(selectedHour) + ":" + pad(selectedMinute);
                                    Toast.makeText(mContext, "Time changed to " + finalTime + "!", Toast.LENGTH_SHORT).show();
                                    //mUserAnswerView.setText(finalTime);
                                    mTimeList.add(finalTime);
                                }
                            }, hour, minute, false);
                    tpd.show();
                }
            });

            mUserAnswerView.setVisibility(View.INVISIBLE);
            layout.addView(timeDialog);
        }

        //TODO: filler comment to add calendar option here

        final Context lastContext = mContext;
        final int lastPosition = position;
        layout.setOnClickListener(new OnClickListener(){
        @Override
        public void onClick(View v) {
            if (lastPosition == (numberOfPages - 1)) {
                if(mTimeList.size() == 0)
                {
                    mTimeList.add("");
                }

                Intent setActivityIntent = new Intent(lastContext, AlarmSetActivity.class);
                setActivityIntent.putExtra("TIME", mTimeList.get(mTimeList.size() - 1));
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
    }

    public void setHiddenTextView() {
        if(mTimeList != null) {
            mUserAnswerView.setText(mTimeList.get(mTimeList.size() - 1));
        }
    }

    /**
     * Function for padding zeros to our time
     * @param c
     * @return
     */
    private static String pad (int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}
