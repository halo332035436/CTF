package com.bullb.ctf.TargetManagement;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.Sales;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.PagerAdapter;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.CalendarView;
import com.bullb.ctf.Widget.MenuView;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TypeATargetDetailActivity extends AppCompatActivity implements CalendarView.OnCalendarClickListener{
    private User user;
    private TextView typeText;
    private int type;
    private ViewPager viewPager;
    private CircleIndicator indicator;
    private List<Fragment> fragments = new ArrayList<>();
    private PagerAdapter adapter;
    private CalendarView calendarView;

    private ApiService apiService;
    private Call<BaseResponse> getTargetPageTask;
    private KeyTools keyTools;
    private AVLoadingIndicatorView progress;
    private UserTargetData userTargetData;
    private SalesData salesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_detail);
        user = SharedPreference.getUser(this);
        apiService = ServiceGenerator.createService(ApiService.class, this);
        keyTools = KeyTools.getInstance(this);
        initUi();

        Gson gson = new Gson();
        userTargetData = gson.fromJson(getIntent().getStringExtra("target"), UserTargetData.class);
        salesData = gson.fromJson(getIntent().getStringExtra("sales"), SalesData.class);

        if (userTargetData != null && salesData != null){
            setData();
        }
        else {
            getTarget();
        }
    }



    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        typeText = (TextView)findViewById(R.id.type_text);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        indicator = (CircleIndicator)findViewById(R.id.indicator);
        calendarView = (CalendarView)findViewById(R.id.calendar_view);
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);

        calendarView.setYear(getIntent().getIntExtra("year",calendarView.getYear()));
        calendarView.setMonth(getIntent().getIntExtra("month",calendarView.getMonth()));
        calendarView.setCalendarClickListener(this);

        setToolbar();
        setBottomView();
        setViewPager();
    }

    private void getTarget(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTarget();
            }
        };

        if (getTargetPageTask != null)
            getTargetPageTask.cancel();

        progress.setVisibility(View.VISIBLE);
        final Call<BaseResponse> getTargetPageSubTask = apiService.getMyTargetPageTask("Bearer " + SharedPreference.getToken(TypeATargetDetailActivity.this),calendarView.getStartDate(), calendarView.getEndDate(), user.id,"percentage");
        getTargetPageTask = getTargetPageSubTask;

        getTargetPageSubTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getTargetPageSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        MyTargetPageResponse temp = new Gson().fromJson(data, MyTargetPageResponse.class);
                        userTargetData = new UserTargetData(temp.user_targets, 0, 0, calendarView.getStartDate());
                        salesData = new SalesData(temp.user_sales,new Sales(),new Sales(),TypeATargetDetailActivity.this, calendarView.getStartDate());
                        setData();
                    } else {
                        SharedUtils.handleServerError(TypeATargetDetailActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getTargetPageSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetry(TypeATargetDetailActivity.this, retry);
                }
            }
        });
    }

    private void setData(){
        for (int i=0; i<fragments.size();i++){
            CircleData data1 = null, data2 = null;
            double rate1 =0, rate2 =0;
            if (i==0){
                data1 = new CircleData(getString(R.string.circle_detail_distributed_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_A()), CircleData.TYPE_MONEY);
                data2 = new CircleData(getString(R.string.circle_detail_adjust_target), SharedUtils.addCommaToNum("+" ,userTargetData.getUserAdditionA()), CircleData.TYPE_TEXT);
                rate1 = userTargetData.getDistributedARate(salesData);
                rate2 = userTargetData.getAdjustedARate(salesData);
            }
            else if (i==1){
                data1 = new CircleData(getString(R.string.circle_detail_distributed_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_F()), CircleData.TYPE_MONEY);
                data2 = new CircleData(getString(R.string.circle_detail_adjust_target), SharedUtils.addCommaToNum("+" ,userTargetData.getUserAdditionF()), CircleData.TYPE_TEXT);
                rate1 = userTargetData.getDistributedFRate(salesData);
                rate2 = userTargetData.getAdjustedFRate(salesData);
            }else if (i==2){
                data1 = new CircleData(getString(R.string.circle_detail_distributed_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_E()), CircleData.TYPE_MONEY);
                data2 = new CircleData(getString(R.string.circle_detail_adjust_target), SharedUtils.addCommaToNum("+" ,userTargetData.getUserAdditionE()), CircleData.TYPE_TEXT);
                rate1 = userTargetData.getDistributedERate(salesData);
                rate2 = userTargetData.getAdjustedERate(salesData);
            }else if (i==3){
                data1 = new CircleData(getString(R.string.circle_detail_distributed_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_M()), CircleData.TYPE_MONEY);
                data2 = new CircleData(getString(R.string.circle_detail_adjust_target), SharedUtils.addCommaToNum("+" ,userTargetData.getUserAdditionM()), CircleData.TYPE_TEXT);
                rate1 = userTargetData.getDistributedMRate(salesData);
                rate2 = userTargetData.getAdjustedMRate(salesData);
            }
            ((TypeATargetDetailFragment)fragments.get(i)).setData(data1, data2,rate1,rate2);
        }
    }

    private void setViewPager(){
        Gson gson = new Gson();
        CircleData data1 = new CircleData(getString(R.string.circle_detail_distributed_target), "", CircleData.TYPE_MONEY);
        CircleData data2 = new CircleData(getString(R.string.circle_detail_adjust_target), "", CircleData.TYPE_TEXT);

        final TypeATargetDetailFragment fragment1 = TypeATargetDetailFragment.newInstance(gson.toJson(data1),gson.toJson(data2),getString(R.string.target_type_A));
        final TypeATargetDetailFragment fragment2 = TypeATargetDetailFragment.newInstance(gson.toJson(data1),gson.toJson(data2),getString(R.string.target_type_F));
        final TypeATargetDetailFragment fragment3 = TypeATargetDetailFragment.newInstance(gson.toJson(data1),gson.toJson(data2),getString(R.string.target_type_E));

        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);

        if (ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_HK)) {
            final TypeATargetDetailFragment fragmentM = TypeATargetDetailFragment.newInstance(gson.toJson(data1),gson.toJson(data2),getString(R.string.target_type_M));
            fragments.add(fragmentM);
        }

        adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (fragments.size()>position)
                    ((TypeATargetDetailFragment)fragments.get(position)).startCircleViewAnimation();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        CircleIndicator indicator = (CircleIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        String type = getIntent().getStringExtra("type");
        if (type.equals(getString(R.string.target_type_A))){
            viewPager.setCurrentItem(0);
        }
        else if (type.equals(getString(R.string.target_type_F))){
            viewPager.setCurrentItem(1);
        }
        else if (type.equals(getString(R.string.target_type_E))){
            viewPager.setCurrentItem(2);
        }
        else if (type.equals(getString(R.string.target_type_M))){
            viewPager.setCurrentItem(3);
        }
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        if (user.type.equals(User.USER_TYPE_A)) {
            toolbarTitle.setText(R.string.my_target);
        }
        else{
            toolbarTitle.setText(R.string.team_target);
        }
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackwardClick() {
        getTarget();
    }

    @Override
    public void onForwardClick() {
        getTarget();
    }

    private void setBottomView(){
        BottomView bottomView = (BottomView) findViewById(R.id.bottom_view);
        final MenuView menuView = (MenuView) findViewById(R.id.menu_view);
        bottomView.setMenuView(menuView);
    }

    @Override
    protected void onDestroy() {
        if (getTargetPageTask != null){
            getTargetPageTask.cancel();
        }
        super.onDestroy();
    }

}
