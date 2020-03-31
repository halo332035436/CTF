package com.bullb.ctf.SalesRanking.RankingDetail;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.MyTargetPageResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.CalendarView;
import com.bullb.ctf.Widget.MenuView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.joooonho.SelectableRoundedImageView;
import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingDetailActivity extends AppCompatActivity implements CalendarView.OnCalendarClickListener{
    private ApiService apiService;
    private KeyTools keyTools;

    private String userID = null;
    private Integer selectedType = 0;
    private Integer selectedCategory = 0;

    private Call<BaseResponse> getMyTargetPageTask;
    private MyTargetPageResponse myTarget;

    private AVLoadingIndicatorView progress;
    private CalendarView calendarView;
    private TextView userNameText, userPositionText, userPlaceText;
    private SelectableRoundedImageView profileImage;

    private FragmentManager fm;
    private RankingDetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_detail);

        setToolbar();
        setBottomView();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        } else {
            userID = extras.getString("ID");
            selectedType = extras.getInt("SELECTED_TYPE");
            selectedCategory = extras.getInt("CATEGORY");
        }

        initUi();

        apiService = ServiceGenerator.createService(ApiService.class, RankingDetailActivity.this);
        keyTools = KeyTools.getInstance(RankingDetailActivity.this);

        getProfileIcon();

        getMyTargetPageTask(calendarView.getStartDate(), calendarView.getEndDate(), userID);
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.sales_ranking);
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
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);
        calendarView = (CalendarView)findViewById(R.id.calendar_view);
        userNameText = (TextView)findViewById(R.id.user_name_text);
        userPositionText = (TextView)findViewById(R.id.user_position_text);
        userPlaceText = (TextView)findViewById(R.id.user_place_text);
        profileImage = (SelectableRoundedImageView)findViewById(R.id.profile_image);

        calendarView.setType(selectedType);
        calendarView.setCalendarClickListener(this);

        fm = getSupportFragmentManager();
        fragment = RankingDetailFragment.newInstance();
        fm.beginTransaction().replace(R.id.ranking_detail_frame, fragment).commit();
    }

    private void getProfileIcon() {
        if (SharedUtils.appIsHongKong()&& SharedPreference.getUser(RankingDetailActivity.this).type.equals(User.USER_TYPE_A)){
            Glide.with(this)
                    .load(R.drawable.user_placeholder)
                    .apply(new RequestOptions().placeholder(R.drawable.user_placeholder))
                    .into(profileImage);
        }else {
            Glide.with(this)
                    .load(userID)
                    .apply(new RequestOptions().placeholder(R.drawable.user_placeholder))
                    .into(profileImage);
        }
    }


    /**
     * @param from_date
     * @param to_date
     * @param user_id another user instead of current user
     */
    private void getMyTargetPageTask(final String from_date, final String to_date, final String user_id) {
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getMyTargetPageTask(from_date, to_date, user_id);
            }
        };

        if (getMyTargetPageTask != null) getMyTargetPageTask.cancel();

        progress.setVisibility(View.VISIBLE);
        final Call<BaseResponse> myGetMyTargetPageTask = apiService.getMyTargetPageTask(
                "Bearer " + SharedPreference.getToken(RankingDetailActivity.this), from_date, to_date, user_id, "sale");

        getMyTargetPageTask = myGetMyTargetPageTask;

        myGetMyTargetPageTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!myGetMyTargetPageTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        Gson gson = new Gson();
                        myTarget = gson.fromJson(data, MyTargetPageResponse.class);
                        setData(myTarget);
                    } else {
                        SharedUtils.handleServerError(RankingDetailActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!myGetMyTargetPageTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetry(RankingDetailActivity.this, retry);
                }
            }
        });
    }

    private void setData(MyTargetPageResponse myTarget) {
        if (SharedUtils.appIsHongKong()&& SharedPreference.getUser(RankingDetailActivity.this).type.equals(User.USER_TYPE_A)){

        }else{
            userNameText.setText(myTarget.name);
            userPositionText.setText(myTarget.title);
        }
        userPlaceText.setText(myTarget.department.long_description);

        fragment.setData(myTarget);
    }

    @Override
    public void onBackwardClick() {
        getMyTargetPageTask(calendarView.getStartDate(), calendarView.getEndDate(), userID);
    }

    @Override
    public void onForwardClick() {
        getMyTargetPageTask(calendarView.getStartDate(), calendarView.getEndDate(), userID);
    }

    @Override
    protected void onDestroy() {
        if (getMyTargetPageTask != null) getMyTargetPageTask.cancel();
        super.onDestroy();
    }

    public int getSelectedCategory(){
        return selectedCategory;
    }
}
