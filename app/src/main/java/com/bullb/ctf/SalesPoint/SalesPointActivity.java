package com.bullb.ctf.SalesPoint;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Reward;
import com.bullb.ctf.Model.RewardData;
import com.bullb.ctf.Model.RewardDetail;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.CalendarView;
import com.bullb.ctf.Widget.MenuView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SalesPointActivity extends AppCompatActivity implements CalendarView.OnCalendarClickListener{
    private CalendarView calendarView;
    private TextView dateText, pointText, titleText;
    private RecyclerView recyclerView;
    private ArrayList<RewardDetail> dataList;
    private SalesPointRecyclerViewAdapter adapter;
    private ApiService apiService;
    private KeyTools keyTools;
    private Call<BaseResponse> getDetailTask;
    private Call<BaseResponse> getRewardRemainTask;
    private Calendar cal;
    private RewardData rewardData;
    private User user;
    private AVLoadingIndicatorView progress;
    private Toast noDataToast;
    private int apiNum = 0;
    private int apiFinish = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_point);

        apiService = ServiceGenerator.createService(ApiService.class, this);
        keyTools = KeyTools.getInstance(this);
        cal = Calendar.getInstance();
        user = SharedPreference.getUser(this);
        initUi();

        rewardData = SharedPreference.getRewards(this);
        changeMonth();
        if (rewardData !=null){
            adapter.setRemaining(rewardData.getRewardRemain());
        }
    }


    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        calendarView = (CalendarView)findViewById(R.id.calendar_view);
        dateText = (TextView)findViewById(R.id.date_text);
        pointText = (TextView)findViewById(R.id.point_text);
        titleText = (TextView)findViewById(R.id.title_text);
        recyclerView = (RecyclerView)findViewById(R.id.sales_point_recyclerview);
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);

        setToolbar();
        setBottomView();

        calendarView.setCalendarClickListener(this);

        dataList = new ArrayList<>();


        adapter = new SalesPointRecyclerViewAdapter(this, dataList);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void getRewardRemainTask(final String fromDate){
        apiNum ++;
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getRewardRemainTask(fromDate);
            }
        };

        if (getRewardRemainTask != null)
            getRewardRemainTask.cancel();
        if (noDataToast != null)
            noDataToast.cancel();

        progress.setVisibility(View.VISIBLE);

        final Call<BaseResponse> myGetRewardRemainTask = apiService.getRewardTask("Bearer " + SharedPreference.getToken(this), user.id,fromDate);
        getRewardRemainTask = myGetRewardRemainTask;
        myGetRewardRemainTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!myGetRewardRemainTask.isCanceled()) {
                    apiFinish ++ ;
                    checkApiFinish();
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        Reward temp = new Gson().fromJson(data, Reward.class);
                        if (temp != null)
                            adapter.setRemaining(temp.amount);
//                            noDataToast = Toast.makeText(SalesPointActivity.this, R.string.no_data, Toast.LENGTH_SHORT);
//                            noDataToast.show();
                    } else {
                        SharedUtils.handleServerError(SalesPointActivity.this, response);
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!myGetRewardRemainTask.isCanceled()) {
                    apiFinish ++ ;
                    checkApiFinish();
                    SharedUtils.networkErrorDialogWithRetryUncancellable(SalesPointActivity.this, retry);
                }
            }
        });
    }

    private void checkApiFinish(){
        if (apiNum == apiFinish){
            progress.setVisibility(View.INVISIBLE);
            apiNum = 0;
            apiFinish = 0;
        }
    }

    private void changeMonth(){
        apiNum = 0;
        apiFinish = 0;
        getRewardRemainTask(calendarView.getStartDate());
        getDetail(calendarView.getStartDate(), calendarView.getEndDate());
    }

    private void getDetail(final String from_date, final String to_date){
        apiNum ++;
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDetail(from_date, to_date);
            }
        };

        if (getDetailTask != null)
            getDetailTask.cancel();
        if (noDataToast != null)
            noDataToast.cancel();

        progress.setVisibility(View.VISIBLE);

        final Call<BaseResponse> myGetRewardDetailTask = apiService.getUserRewardDetailTask("Bearer " + SharedPreference.getToken(this),user.id, from_date, to_date);
        getDetailTask = myGetRewardDetailTask;

        myGetRewardDetailTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!myGetRewardDetailTask.isCanceled()) {
                    apiFinish ++ ;
                    checkApiFinish();
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        ArrayList<RewardDetail> temp = new Gson().fromJson(data, new TypeToken<ArrayList<RewardDetail>>() {}.getType());
                        dataList.clear();
                        dataList.addAll(temp);
                        adapter.notifyDataSetChanged();
                    } else {
                        SharedUtils.handleServerError(SalesPointActivity.this, response);
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!myGetRewardDetailTask.isCanceled()) {
                    apiFinish ++ ;
                    checkApiFinish();
                    progress.setVisibility(View.INVISIBLE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(SalesPointActivity.this, retry);
                }
            }
        });
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        if(ServerPreference.getServerVersion(SalesPointActivity.this).equals(ServerPreference.SERVER_VERSION_CN)){
            toolbarTitle.setText(R.string.sales_mark);
        }else{
            toolbarTitle.setText(R.string.hk_sales_mark);
        }
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

    @Override
    public void onBackwardClick() {
        changeMonth();
    }

    @Override
    public void onForwardClick() {
        changeMonth();
    }

    @Override
    protected void onDestroy() {
        if (getDetailTask != null){
            getDetailTask.cancel();
        }
        if (getRewardRemainTask != null){
            getRewardRemainTask.cancel();
        }
        super.onDestroy();
    }


}
