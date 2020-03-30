package com.bullb.ctf.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.bullb.ctf.Notification.NotificationActivity;

/**
 * Created by oscarlaw on 28/7/16.
 */
public class WidgetUtils {
    public static class BackButtonListener implements View.OnClickListener {
        public Context context;

        public BackButtonListener(Context context){
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            ((Activity)context).onBackPressed();
        }

    }

    public static class NotificationButtonListener implements View.OnClickListener {
        public Context context;

        public NotificationButtonListener(Context context){
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(context, NotificationActivity.class);
            context.startActivity(intent);

        }

    }


}
