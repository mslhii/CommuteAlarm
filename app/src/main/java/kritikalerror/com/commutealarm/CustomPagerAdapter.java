package kritikalerror.com.commutealarm;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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

    int NumberOfPages = 5;

    Context mContext;

    int[] res = {
            android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_compass,
            android.R.drawable.ic_menu_directions,
            android.R.drawable.ic_menu_gallery
    };

    int[] backgroundcolor = {
            0xFF101010,
            0xFF202020,
            0xFF303030,
            0xFF404040,
            0xFF505050
    };

    public CustomPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return NumberOfPages;
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
        textView.setText(String.valueOf(position));

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
        layout.addView(imageView);

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
        container.removeView((LinearLayout)object);
    }

}
