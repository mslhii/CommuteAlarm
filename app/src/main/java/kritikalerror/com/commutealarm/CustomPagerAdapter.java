package kritikalerror.com.commutealarm;

import android.content.Context;
import android.content.Intent;
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

    public final int numberOfPages = 4;

    private Context mContext;
    public EditText mUserAnswerView;

    private int[] backgroundColor = {
            0xFF101010,
            0xFF202020,
            0xFF303030,
            0xFF404040,
    };

    private String[] questionStrings = {
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

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        layout.setBackgroundColor(backgroundColor[position]);
        layout.setLayoutParams(layoutParams);
        layout.addView(textView);

        if(!(position == (numberOfPages - 1)))
        {
            mUserAnswerView = new EditText(mContext);
            mUserAnswerView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            mUserAnswerView.setTag("textview");
            layout.addView(mUserAnswerView);
        }

        final Context lastContext = mContext;
        final int lastPosition = position;
        layout.setOnClickListener(new OnClickListener(){
        @Override
        public void onClick(View v) {
            //Toast.makeText(lastContext, "Page clicked", Toast.LENGTH_LONG).show();

            if (lastPosition == (numberOfPages - 1)) {
                Intent setActivityIntent = new Intent(lastContext, AlarmSetActivity.class);
                setActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
                //setActivityIntent.setFlags(setActivityIntent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
                //setActivityIntent.setFlags(setActivityIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                lastContext.startActivity(setActivityIntent);
            }
        }});

        container.addView(layout);
        layout.setTag("pos" + position);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //Toast.makeText(mContext,
        //        "Page " + position + " destroyed",
        //        Toast.LENGTH_LONG).show();
        container.removeView((LinearLayout)object);
    }

}
