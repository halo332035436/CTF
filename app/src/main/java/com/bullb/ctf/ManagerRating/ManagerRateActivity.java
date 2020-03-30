package com.bullb.ctf.ManagerRating;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Score;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.SelfManagement.SelfManagementActivity;
import com.bullb.ctf.Utils.ImageCache;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.CalendarView;
import com.bullb.ctf.Widget.MenuView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerRateActivity extends AppCompatActivity implements CalendarView.OnCalendarClickListener{
    private ApiService apiService;
    private KeyTools keyTools;

    private User user;
    private Score score;
    private Call<BaseResponse> getProfileImageTask;
    private Call<BaseResponse> getScoreTask;

    private ManagerRateFragment fragment;
    private CalendarView calendarView;
    private AVLoadingIndicatorView progress;
    private RelativeLayout loadingLayout;

    private ImageView profileImage;
    private TextView userNameText;
    private TextView userPositionText;
    private TextView userLocationText;

    private int MANAGER_RATE_RESULT_CODE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_rate);

        setToolbar();
        setBottomView();
        initUi();

        user = SharedPreference.getUser(this);
        Bundle data = getIntent().getExtras();
        if (getIntent().getExtras() != null)
            score = (Score) data.getSerializable("SCORE");

        apiService = ServiceGenerator.createService(ApiService.class,this);
        keyTools = KeyTools.getInstance(this);

        setUIData();
    }


    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.manager_judgement);
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
    }

    private void setBottomView(){
        BottomView bottomView = (BottomView) findViewById(R.id.bottom_view);
        final MenuView menuView = (MenuView) findViewById(R.id.menu_view);
        bottomView.setMenuView(menuView);
    }

    private void initUi(){
        // Hide unwanted layout in this Activity
        LinearLayout personalInfoLayout = (LinearLayout) findViewById(R.id.personal_info_banner_layout);
        LinearLayout teamInfoLayout = (LinearLayout)findViewById(R.id.team_info_banner_layout);
        ImageView breakdown_btn = (ImageView)findViewById(R.id.breakdown_btn);
        personalInfoLayout.setVisibility(View.VISIBLE);
        teamInfoLayout.setVisibility(View.GONE);
        breakdown_btn.setVisibility(View.GONE);

        // setup UI
        userNameText = (TextView) findViewById(R.id.user_name_text);
        userPositionText = (TextView)findViewById(R.id.user_position_text);
        userLocationText = (TextView)findViewById(R.id.user_place_text);
        profileImage = (ImageView)findViewById(R.id.profile_image);
        calendarView = (CalendarView)findViewById(R.id.calendar_view);
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
    }

    private void setUIData() {
        // display name, title, unit
        if (user.type.equals(User.USER_TYPE_A)){
            userNameText.setText(user.name);
            userPositionText.setText(user.title);
            userLocationText.setText(user.getLongDepartmentName());
        }

        if (user.type.equals(User.USER_TYPE_B) && score != null){
            userNameText.setText(score.user.name);
            userPositionText.setText(score.user.title);
            userLocationText.setText(user.getLongDepartmentName());
        }

        // display icon
        Glide.with(this).load(user.getIconUrl()).placeholder(R.drawable.user_placeholder).dontAnimate().into(profileImage);

//        byte[] image = imageCache.getBitmapFromMemCache(user.id);
//        if (image!= null)
//            Glide.with(ManagerRateActivity.this).load(image).dontAnimate().into(profileImage);
//        else
//            getProfileImage();

        // Set Fragment
        FragmentManager fm = getSupportFragmentManager();
        fragment = ManagerRateFragment.newInstance(score);
        fm.beginTransaction().replace(R.id.managerRateFrame, fragment).commit();

        // Calendar view
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        calendarView.setCurrentCalendar(cal);
        calendarView.setCalendarClickListener(this);
    }

    private void getMyScore(final String month){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getMyScore(month);
            }
        };

        if (getScoreTask != null) getScoreTask.cancel();

        final String userId;
        if (user.type.equals(User.USER_TYPE_A)){
            userId = user.id;
        } else {
            userId = score.getUserId();
        }

        progress.setVisibility(View.VISIBLE);

        final Call<BaseResponse> getThisScoreTask = apiService.getScoresTask(
                "Bearer " + SharedPreference.getToken(this), month, userId, null);

        getScoreTask = getThisScoreTask;

        getThisScoreTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getThisScoreTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        Score[] ss = new Gson().fromJson(data, Score[].class);
                        Score s = (ss.length > 0) ? ss[0] : null;
                        try {
                            setScore(s);
                        } catch (Exception e) {
                            Log.e("Error", "setScore error", e);
                        }
                    } else {
                        SharedUtils.handleServerError(ManagerRateActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getThisScoreTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetry(ManagerRateActivity.this, retry);
                }
            }
        });
    }

    public void monthChange(){
        getMyScore(SharedUtils.getMonthForAPI(calendarView));
    }

    public void setScore(Score score){
        fragment.setData(score);
    }

//    /**
//     * Get user icon from API if not found in cache
//     */
//    private void getProfileImage(){
//        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                getProfileImage();
//            }
//        };
//        getProfileImageTask = apiService.getUserProfileTask( "Bearer " + SharedPreference.getToken(this), user.id);
//        getProfileImageTask.enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                if (response.isSuccessful()){
//                    byte[] imageByte = keyTools.decryptImage(response.body().iv, response.body().data);
//                    imageCache.addBitmapToMemoryCache(user.id, imageByte);
//                    Glide.with(ManagerRateActivity.this).load(imageByte).dontAnimate().into(profileImage);
//                } else{
//                    SharedUtils.handleServerError(ManagerRateActivity.this, response);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                SharedUtils.networkErrorDialogWithRetry(ManagerRateActivity.this, retry);
//            }
//        });
//    }

    /**
     * Get User object for ManagerRateFragment
     * @return user object or null
     */
    public User getUser(){
        if (this.user != null) return user;
        return null;
    }

    /**
     * getCalendarView
     * @return
     */
    public CalendarView getCalendarView(){
        if (calendarView != null) return calendarView;
        return null;
    }

    /**
     * Get KeyTools object for ManagerRateFragment
     * @return
     */
    public KeyTools getKeyTools(){
        if (this.keyTools != null) return keyTools;
        return null;
    }

    /**
     * Get ApiService object reference for for ManagerRateFragment
     * @return
     */
    public ApiService getApiService(){
        if (this.apiService != null) return apiService;
        return null;
    }

    /**
     * Block the UI when calling EditScore API in ManagerRateFragment
     * @param b
     */
    public void setLoading(boolean b){
        SharedUtils.loading(loadingLayout, b);
    }

    @Override
    public void onBackwardClick() {
        monthChange();
    }

    @Override
    public void onForwardClick() {
        monthChange();
    }

    @Override
    protected void onDestroy() {
        if (getProfileImageTask != null) getProfileImageTask.cancel();
        if (getScoreTask != null) getScoreTask.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("SCORE", score);
        i.putExtras(b);
        setResult(MANAGER_RATE_RESULT_CODE, i);
        finish();
    }
}
