package com.bullb.ctf.JPush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.Login.SplashActivity;
import com.bullb.ctf.MainApplication;
import com.bullb.ctf.Notification.PushNotificationActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.WebView.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    private NotificationManager nm;

    public MyReceiver() {
        super();
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }

        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle);

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush用户注册成功");
            String channelId = JPushInterface.getRegistrationID(context);
            Log.d(TAG, "onReceive: "+channelId);
            SharedPreference.setChannelId(context,channelId);
            if(BuildConfig.DEBUG) {
                Toast.makeText(context, "极光ID: " + channelId, Toast.LENGTH_SHORT).show();
            }
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的通知");

            receivingNotification(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");

            openNotification(context, bundle);

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private void receivingNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
        Log.d("push", "arrived");
        int notificationNum = SharedPreference.getNotificationUnreadCount(context);
        notificationNum ++;
        SharedPreference.setNotificationUnreadCount(context, notificationNum);
    }

    private void openNotification(Context context, Bundle bundle) {
//        if(SharedPreference.getToken(context)!=null) {
//            Intent mIntent = new Intent(context, PushNotificationActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            stackBuilder.addNextIntentWithParentStack(mIntent);
//
//            mIntent.putExtras(bundle);
//            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            try {
//                pendingIntent.send();
//            } catch (PendingIntent.CanceledException e) {
//                e.printStackTrace();
//            }
//        }else{
//            PackageManager pm = context.getPackageManager();
//            Intent launchIntent = pm.getLaunchIntentForPackage("com.bullb.ctf");
//            context.startActivity(launchIntent);
//        }
        try {
            Intent intent = new Intent();
            JSONObject jObject = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
            String type = jObject.getString("event_type");
            String id = jObject.getString("id");
            if (type == null || id == null)
                return;

            intent.setClass(context, SplashActivity.class);

            if (((MainApplication)context.getApplicationContext()).isNotificationStartInSplash()){
                intent.setClass(context,SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            else{
                if (type.equals("notification")){
                    intent.setClass(context,WebViewActivity.class);
                    Log.d("push", "notice");
                }
                else if (type.equals("campaign")){
                    intent.setClass(context, WebViewActivity.class);
                    Log.d("push", "campaign");
                }
            }
            intent.putExtra("is_notification", true);
            intent.putExtra("title", bundle.getString(JPushInterface .EXTRA_NOTIFICATION_TITLE));
            intent.putExtra("n_id", id);
            intent.putExtra("type", type);
            Log.d("push", "start");

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateChannel(final Context context){
        ApiService apiService = ServiceGenerator.createService(ApiService.class,context);
        KeyTools keyTools = KeyTools.getInstance(context);
        Map<String, String> dataMap = new HashMap<>();
        String channelId = JPushInterface.getRegistrationID(context);
        dataMap = keyTools.encrypt(channelId);
        dataMap.put("device_key", keyTools.getPublicKey());
        Call<BaseResponse> updateChannelTask = apiService.updateChannelTask("Bearer " + SharedPreference.getToken(context), "text/plain","text/plain",dataMap);
        updateChannelTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                }
                else{
                    SharedUtils.handleServerError(context, response);
                }

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                SharedUtils.networkErrorDialogWithRetry(context, retry);

            }
        });
    }


}
