package com.bullb.ctf.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedPreference;

/**
 * Created by oscarlaw on 5/10/2016.
 */

public class NotificationIndicator extends RelativeLayout{
    private View redDot;

    public NotificationIndicator(Context context) {
        super(context);
        init();
    }

    public NotificationIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotificationIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.notification_indicator_layout, this);
        redDot = findViewById(R.id.red_dot);
        checkUnread();
    }

    public void checkUnread(){
        if (SharedPreference.getNotificationUnread(getContext())){
            redDot.setVisibility(VISIBLE);
        }
        else{
            redDot.setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE){
            //onResume called
            checkUnread();
        }
        else{
            // onPause() called
            checkUnread();
        }
    }
}
