package com.bullb.ctf.Notification;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.Model.Notification;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserNotification;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.WebView.WebViewActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotificationActivity extends AppCompatActivity {
    private PushNotificationRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
//    private ArrayList<Notification> dataList;
    private ArrayList<UserNotification> dataList;
    private AVLoadingIndicatorView progress;

    private ApiService apiService;
    private KeyTools keyTools;
    private Call<BaseResponse> getNotificationTask;
    private Call<BaseResponse> getUserTask;

    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        keyTools = KeyTools.getInstance(this);
        apiService = ServiceGenerator.createService(ApiService.class, this);

        dataList = new ArrayList<>();
        gson = new Gson();

        setToolbar();
        initUi();
        getNotificationList();

//        String notificationJson = getIntent().getExtras().getString(JPushInterface.EXTRA_EXTRA);
//        if(notificationJson!=null){
//            Log.d("Push", notificationJson);
//            try {
//                JSONObject obj = new JSONObject(notificationJson);
//                Intent intent = new Intent(this, WebViewActivity.class);
//                intent.putExtra("title", obj.get("name").toString());
//                intent.putExtra("n_id",obj.get("id").toString());
//                intent.putExtra("type",obj.get("type").toString());
//                startActivity(intent);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getUser();
    }

    private void getUser(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUser();
            }
        };

        getUserTask = apiService.getCurrentUserTask("Bearer " + SharedPreference.getToken(this));
        getUserTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv,response.body().data);
                    User user = gson.fromJson(data, User.class);

                    //set Unread Notification
                    if (user.unread_notifications_count > 0){
                        SharedPreference.setNotificationUnread(PushNotificationActivity.this, true);
                    }else{
                        SharedPreference.setNotificationUnread(PushNotificationActivity.this, false);
                    }
                    SharedPreference.setNotificationUnreadCount(PushNotificationActivity.this, user.unread_notifications_count);


                } else{
//                    SharedUtils.networkErrorDialogWithRetryUncancellable(PushNotificationActivity.this, retry);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                SharedUtils.networkErrorDialogWithRetryUncancellable(PushNotificationActivity.this, retry);
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.push_notification);
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.GONE);
    }

    private void initUi(){
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);

        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        recyclerView = (RecyclerView)findViewById(R.id.notification_recyclerview);

        adapter = new PushNotificationRecyclerViewAdapter(this, dataList);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getNotificationList() {
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getNotificationList();
            }
        };

        if (getNotificationTask != null) getNotificationTask.cancel();

        progress.setVisibility(View.VISIBLE);

        final Call<BaseResponse> myNotificationListTask = apiService.getNotificationListTask("Bearer " + SharedPreference.getToken(this));
        getNotificationTask = myNotificationListTask;
        myNotificationListTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!myNotificationListTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        ArrayList<UserNotification> temp = new Gson().fromJson(data, new TypeToken<ArrayList<UserNotification>>(){}.getType());
                        dataList.addAll(temp);
                        adapter.notifyDataSetChanged();
//                        //clear notification count
//                        SharedPreference.setNotificationUnread(PushNotificationActivity.this, false);
//                        SharedPreference.setNotificationUnreadCount(PushNotificationActivity.this, 0);

                    } else {
                        SharedUtils.handleServerError(PushNotificationActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!myNotificationListTask.isCanceled()){
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(PushNotificationActivity.this, retry);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (getNotificationTask != null) getNotificationTask.cancel();
        super.onDestroy();
    }
}
