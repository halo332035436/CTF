package com.bullb.ctf.MyTeam;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.CalendarView;
import com.bullb.ctf.Widget.MenuView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTeamActivity extends AppCompatActivity implements View.OnClickListener, ObservableScrollViewCallbacks, CalendarView.OnCalendarClickListener, SearchView.OnQueryTextListener{
    private User user;
    private ApiService apiService;
    private KeyTools keyTools;

    private AVLoadingIndicatorView progress;
    private TextView teamLocationText;
    private CalendarView calendarView;
    private SearchView search_view;

    private Call<BaseResponse> getScoreTask;
    private MyTeamFragment fragment;

    private static final int MANAGER_RATE_REQUEST_CODE = 1;
    private static final int MANAGER_RATE_RESULT_CODE = 2;
    public int edit_position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);

        keyTools = KeyTools.getInstance(this);
        apiService = ServiceGenerator.createService(ApiService.class, this);

        setToolbar();
        setBottomView();
        initUi();

        user = SharedPreference.getUser(this);
        if (user == null) return;

        setUIData();
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.my_team);
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
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);
        teamLocationText = (TextView)findViewById(R.id.team_location);
        calendarView = (CalendarView)findViewById(R.id.calendar_view);
        search_view = (SearchView) findViewById(R.id.search_view);
        customizeSearchView(search_view);

        LinearLayout personalInfoLayout = (LinearLayout) findViewById(R.id.personal_info_banner_layout);
        LinearLayout teamInfoLayout = (LinearLayout)findViewById(R.id.team_info_banner_layout);
        ImageView breakdown_btn = (ImageView)findViewById(R.id.breakdown_btn);

        // hide some unwanted shared elements
        personalInfoLayout.setVisibility(View.GONE);
        teamInfoLayout.setVisibility(View.VISIBLE);
        breakdown_btn.setVisibility(View.GONE);
    }

    private void customizeSearchView(SearchView search_view) {
        ImageView searchViewIcon =
                (ImageView)search_view.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);

        ViewGroup linearLayoutSearchView =
                (ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);
        linearLayoutSearchView.addView(searchViewIcon);

        View v = search_view.findViewById(android.support.v7.appcompat.R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT );
    }

    private void setUIData() {
        teamLocationText.setText(user.getLongDepartmentName());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        calendarView.setCurrentCalendar(cal);
        calendarView.setCalendarClickListener(this);
        search_view.setOnQueryTextListener(this);
        getListData(SharedUtils.getMonthForAPI(calendarView));
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        adapter.scrollImage(scrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onBackwardClick() {
        monthChange();
    }

    @Override
    public void onForwardClick() {
        monthChange();
    }

    private void monthChange(){
        getListData(SharedUtils.getMonthForAPI(calendarView));
    }

    private void getListData(final String fromDate){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getListData(fromDate);
            }
        };

        if (getScoreTask != null) getScoreTask.cancel();

        progress.setVisibility(View.VISIBLE);

        final Call<BaseResponse> myScoreTask = apiService.getScoresTask(
                "Bearer " + SharedPreference.getToken(this), fromDate, null, user.viewable_root_department.id);

        getScoreTask = myScoreTask;
        myScoreTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!myScoreTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        Score[] scores = new Gson().fromJson(data, Score[].class);
                        setData(scores);
                    } else {
                        SharedUtils.handleServerError(MyTeamActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!myScoreTask.isCanceled()){
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(MyTeamActivity.this, retry);
                }
            }
        });
    }

    private void setData(Score[] scores){
        TextView e = (TextView) findViewById(R.id.is_empty);
        FrameLayout f = (FrameLayout) findViewById(R.id.myTeamFrame);
        if (scores.length == 0){
            f.setVisibility(View.GONE);
            e.setVisibility(View.VISIBLE);
            return;
        }
        f.setVisibility(View.VISIBLE);
        e.setVisibility(View.GONE);

        FragmentManager fm = getSupportFragmentManager();
        fragment = MyTeamFragment.newInstance(scores, user.department_id);
        fm.beginTransaction().replace(R.id.myTeamFrame, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == MANAGER_RATE_REQUEST_CODE) {
            if (resultCode == MANAGER_RATE_RESULT_CODE) {
                Bundle b = i.getExtras();
                if (b != null){
                    Score score = (Score) b.getSerializable("SCORE");
                    fragment.setScore(score);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String keyword) {
        search(keyword);
        return false;
    }

    private void search(String keyword){
        fragment.adapter.filterList(keyword);
    }
}
