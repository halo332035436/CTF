package com.bullb.ctf;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.JPush.MyReceiver;
import com.bullb.ctf.Model.Banner;
import com.bullb.ctf.Model.Campaign;
import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.KeyValue;
import com.bullb.ctf.Model.LandingMenuItem;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserRole;
import com.bullb.ctf.Notification.PushNotificationActivity;
import com.bullb.ctf.Setting.SettingActivity;
import com.bullb.ctf.Utils.BannerPagerAdapter;
import com.bullb.ctf.Utils.CampaignPagerAdapter;
import com.bullb.ctf.Utils.CustomViewPager;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.WebView.WebViewActivity;
import com.bullb.ctf.Widget.NotificationIndicator;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingPageActivity extends AppCompatActivity{
    private ImageView bannerImage;
    private RecyclerView recyclerView;
    private ArrayList<LandingMenuItem> menuList;
    private MenuAdapter adapter;
    private ApiService apiService;

    private Call<BaseResponse> getUserTask;
    private RelativeLayout loadingLayout;
    private KeyTools keyTools;
    private Gson gson;
    private Call<BaseResponse> refreshTokenTask;
    private Call<BaseResponse> getCampaignBannerTask;
    private ProgressBar bannerProgress;

    private ViewPager bannerViewPager;
    private CircleIndicator indicator;
    private CampaignPagerAdapter bannerAdapter;
    private ArrayList<Campaign> bannerList = new ArrayList<>();

    private Handler bannerHandler = new Handler();
    private final static int loopMilli = 3000;

    private User mUser;
    private boolean hasPersonalInformation = true,
                    hasAttendanceManagement = true,
                    hasSetting = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        apiService = ServiceGenerator.createService(ApiService.class,this);
        keyTools = KeyTools.getInstance(this);
        gson = new Gson();
        initUi();
        checkIsLangChange();


        //if auto login, refresh token first
        if (getIntent().getBooleanExtra("open_app",false)){
            refreshToken();
        } else{
            getUser();
            getCampaignBanner();
        }

//        MyReceiver myReceiver = new MyReceiver();
//        myReceiver.updateChannel(this);


    }


    /**
     * Direct page back to setting activity after language changed
     */
    private void checkIsLangChange(){
        if (getIntent().getBooleanExtra("is_lang_change",false)){
            Intent intent = new Intent();
            intent.putExtra("is_lang_change", getIntent().getBooleanExtra("is_lang_change",false));
            intent.setClass(this, SettingActivity.class);
            startActivity(intent);
        }
    }

    private void checkNotification(){
        if (getIntent().getBooleanExtra("is_notification",false)){
            Intent intent = getIntent();
            String type = getIntent().getStringExtra("type");
            if (type == null)
                return;
            else if (type.equals("notice") || type.equals("campaign")){
                intent.setClass(this, WebViewActivity.class);
                startActivity(intent);
            }
        }
        //
    }


    private void getUser(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUser();
            }
        };
        SharedUtils.loading(loadingLayout, true);

        getUserTask = apiService.getCurrentUserTask("Bearer " + SharedPreference.getToken(this));
        getUserTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                SharedUtils.loading(loadingLayout, false);
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv,response.body().data);
                    User user = gson.fromJson(data, User.class);
                    mUser = user;
//                    Department dummy = user.getDepartment();
//                    dummy.id = "2006595";
//                    user.viewable_root_departments.add(dummy);
                    SharedPreference.setUser(user, LandingPageActivity.this);

                    //set Unread Notification
                    if (user.unread_notifications_count > 0){
                        SharedPreference.setNotificationUnread(LandingPageActivity.this, true);
                    }else{
                        SharedPreference.setNotificationUnread(LandingPageActivity.this, false);
                    }
                    SharedPreference.setNotificationUnreadCount(LandingPageActivity.this, user.unread_notifications_count);

                            //update notification btn ui
                    ((NotificationIndicator)findViewById(R.id.red_dot)).checkUnread();

                    ((MainApplication)getApplicationContext()).setNotificationStartInSplash(false);
                    checkNotification();

                    setMenu();

                } else{
                    SharedUtils.handleServerError(LandingPageActivity.this, response);

//                    SharedUtils.networkErrorDialogWithRetryUncancellable(LandingPageActivity.this, retry);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                SharedUtils.loading(loadingLayout, false);
                SharedUtils.networkErrorDialogWithRetryUncancellable(LandingPageActivity.this, retry);
            }
        });
    }

    private void initUi(){
        bannerImage = (ImageView)findViewById(R.id.banner_image);
        recyclerView = (RecyclerView)findViewById(R.id.menu_recycler_view);
        loadingLayout = (RelativeLayout)findViewById(R.id.loading_layout);
        bannerProgress = (ProgressBar)findViewById(R.id.banner_progress);
        bannerViewPager = (ViewPager)findViewById(R.id.banner_view_pager);
        indicator = (CircleIndicator)findViewById(R.id.circle_indicator);

        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setToolbar();

        //set banner image height to 16:9
//        ViewGroup.LayoutParams viewParams = bannerImage.getLayoutParams();
//        viewParams.height = SharedUtils.getBannerHeight(this);
//        bannerImage.setLayoutParams(viewParams);


    }

    private void setMenu(){

        UserRole userRole = mUser.getUserRole();
        ArrayList<KeyValue> keyValues = new ArrayList<>();
        if (userRole != null)
            keyValues = userRole.getDetail();

        if (keyValues != null) {
            for (KeyValue keyValue : keyValues) {
                switch (keyValue.getKey()) {
                    case "personal_information":
                        hasPersonalInformation = keyValue.getValue();
                        break;
                    case "attendance_management":
                        hasAttendanceManagement = keyValue.getValue();
                        break;
                    case "setting":
                        hasSetting = keyValue.getValue();
                        break;
                }
            }
        }

        //set recycler view
        menuList = new ArrayList<>();
        menuList.add(new LandingMenuItem(R.drawable.self_management_enable, getString(R.string.self_management), true));
//        menuList.add(new LandingMenuItem(R.drawable.flow_platform, getString(R.string.flow_platform), false));
//        menuList.add(new LandingMenuItem(R.drawable.assessment_management, getString(R.string.assessment_management), true));
        if(hasAttendanceManagement) {
            menuList.add(new LandingMenuItem(R.drawable.direct_attendance, getString(R.string.direct_assessment), true));

            menuList.add(new LandingMenuItem(R.drawable.assessment_management_active, getString(R.string.assessment_management), true));
        }
        menuList.add(new LandingMenuItem(R.drawable.salary_enquiry, getString(R.string.salary_enquiry), false));
        if(!SharedUtils.appIsHongKong()){
            menuList.add(new LandingMenuItem(R.drawable.customer_service, getString(R.string.customer_service), true));
            menuList.add(new LandingMenuItem(R.drawable.performance_assessment_active, getString(R.string.performance_assessment), true));
        }
        menuList.add(new LandingMenuItem(R.drawable.mobile_learning, getString(R.string.hr_online), false));
        if(hasPersonalInformation) {
            menuList.add(new LandingMenuItem(R.drawable.personal_info, getString(R.string.personal_info), true));
        }
        if(hasSetting) {
            menuList.add(new LandingMenuItem(R.drawable.setting, getString(R.string.setting), true));
        }
        
        adapter = new MenuAdapter(this, menuList, this,null);

        final GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void refreshToken(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refreshToken();
            }
        };

        SharedUtils.loading(loadingLayout, true);
        Map<String, String> dataMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("empty", "empty");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("data json", jsonObject.toString());
        dataMap = keyTools.encrypt(jsonObject.toString());
        refreshTokenTask = apiService.refreshTokenTask("Bearer " + SharedPreference.getToken(this), dataMap);
        refreshTokenTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                SharedUtils.loading(loadingLayout, false);
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv,response.body().data);
                    SharedPreference.setToken(LandingPageActivity.this, data);
                    getUser();
                    getCampaignBanner();
                }else{
                    Toast.makeText(LandingPageActivity.this, getString(R.string.error_code) + ":" + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    SharedPreference.logout(LandingPageActivity.this);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                SharedUtils.loading(loadingLayout, false);
                SharedUtils.networkErrorDialogWithRetryUncancellable(LandingPageActivity.this, retry);
            }
        });
    }



    private void getCampaignBanner(){
//        Glide.with(LandingPageActivity.this).load(R.drawable.advert).into(bannerImage);

        bannerProgress.setVisibility(View.VISIBLE);
        getCampaignBannerTask = apiService.getCampaignBannersTask("Bearer " + SharedPreference.getToken(this));
        getCampaignBannerTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getCampaignBannerTask.isCanceled()) {
                    bannerProgress.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        final Campaign[] banners = new Gson().fromJson(data, Campaign[].class);
                        if(banners.length>0) {
                            bannerList.addAll(Arrays.asList(banners));
//                            bannerList.addAll(Arrays.asList(banners));
//                            bannerList.addAll(Arrays.asList(banners));
                            updateBanner();
                        }else{
                            bannerViewPager.setVisibility(View.GONE);
                        }
//                        if (campaign != null)
//                            Glide.with(LandingPageActivity.this).load(campaign.image_url).into(bannerImage);
//                        bannerImage.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent();
//                                intent.putExtra("title",getString(R.string.sales_activity));
//                                intent.putExtra("data",campaign.details);
//                                intent.setClass(LandingPageActivity.this, WebViewActivity.class);
//                                startActivity(intent);
//                            }
//                        });
                    }else{
                        bannerViewPager.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getCampaignBannerTask.isCanceled()) {
                    bannerProgress.setVisibility(View.INVISIBLE);
                }
                bannerViewPager.setVisibility(View.GONE);
            }
        });
    }


    public void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
    }

    private void updateBanner(){
        bannerAdapter = new CampaignPagerAdapter(LandingPageActivity.this,bannerList);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        indicator.setViewPager(bannerViewPager);
        bannerHandler.postDelayed(loopBanner,loopMilli);
    }

    private Runnable loopBanner = new Runnable() {
        @Override
        public void run() {
            bannerViewPager.setCurrentItem((bannerViewPager.getCurrentItem()+1)%bannerAdapter.getCount());
            bannerHandler.postDelayed(this,loopMilli);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        bannerHandler.removeCallbacks(null);

    }


    @Override
    protected void onDestroy() {
        if (getUserTask != null){
            getUserTask.cancel();
        }
        if (refreshTokenTask != null){
            refreshTokenTask.cancel();
        }
        if (getCampaignBannerTask != null){
            getCampaignBannerTask.cancel();
        }
        super.onDestroy();
    }
}
