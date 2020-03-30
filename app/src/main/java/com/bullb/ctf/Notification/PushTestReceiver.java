package com.bullb.ctf.Notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.bullb.ctf.Login.SplashActivity;
import com.bullb.ctf.MainApplication;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.WebView.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by oscarlaw on 15/12/2016.
 */

public class PushTestReceiver extends PushMessageReceiver {
    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {
        Log.i("push_my_log", "error_code:" + String.valueOf(i) + " appid=" + s +  " userId="+ s1 + " channel_id=" + s2 + " requestId=" + s3);
//        SharedPreference.setChannelId(context, s2);
    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String s, String s1) {
        Log.d("test s", s);
        Log.d("test s1", s1);

    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        Log.d("test s", s);
        Log.d("test s1", s1);
        Log.d("test s2", s2);

        try {
            Intent intent = new Intent();
            JSONObject jObject = new JSONObject(s2);
            String type = jObject.getString("type");
            String id = jObject.getString("id");
            if (type == null || id == null)
                return;

            intent.setClass(context, SplashActivity.class);

            if (((MainApplication)context.getApplicationContext()).isNotificationStartInSplash()){
                intent.setClass(context,SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            else{
                if (type.equals("notice")){
                    intent.setClass(context,WebViewActivity.class);
                    Log.d("push", "notice");
                }
                else if (type.equals("campaign")){
                    intent.setClass(context, WebViewActivity.class);
                    Log.d("push", "campaign");
                }
            }
            intent.putExtra("is_notification", true);
            intent.putExtra("title", s1);
            intent.putExtra("n_id", id);
            intent.putExtra("type", type);
            Log.d("push", "start");

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {
        Log.d("push", "arrived");
        int notificationNum = SharedPreference.getNotificationUnreadCount(context);
        notificationNum ++;
        SharedPreference.setNotificationUnreadCount(context, notificationNum);
    }

}
