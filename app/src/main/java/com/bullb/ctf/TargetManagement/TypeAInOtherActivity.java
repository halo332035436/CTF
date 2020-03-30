package com.bullb.ctf.TargetManagement;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.MyTargetPageResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.PagerAdapter;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BlurCircleView;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.CalendarView;
import com.bullb.ctf.Widget.MenuView;
import com.bullb.ctf.Widget.TabEntity;
import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.joooonho.SelectableRoundedImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TypeAInOtherActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private User user;
    private TextView userNameText, userPositionText, userPlaceText, rankText, rankTitle;
    private SelectableRoundedImageView profileImage;
    private CommonTabLayout tabLayout;
    private View upperLine, lowerLine;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private ApiService apiService;
    private KeyTools keyTools;
    private ArrayList<Fragment> fragments;
    private Call<BaseResponse> getDataTask;
    private AVLoadingIndicatorView progress;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_ain_other);

        user = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        currentUser = SharedPreference.getUser(this);
        apiService = ServiceGenerator.createService(ApiService.class, this);
        keyTools = KeyTools.getInstance(this);
        initUi();
        getData();
    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        calendarView = (CalendarView)findViewById(R.id.calendar_view);
        userNameText = (TextView)findViewById(R.id.user_name_text);
        userPositionText = (TextView)findViewById(R.id.user_position_text);
        userPlaceText = (TextView)findViewById(R.id.user_place_text);
        profileImage = (SelectableRoundedImageView)findViewById(R.id.profile_image);
        tabLayout = (CommonTabLayout)findViewById(R.id.period_tablayout);
        upperLine = findViewById(R.id.upper_tab_line);
        lowerLine = findViewById(R.id.lower_tab_line);
        viewPager = (ViewPager)findViewById(R.id.pager);
        circleIndicator = (CircleIndicator)findViewById(R.id.indicator);
        rankText = (TextView)findViewById(R.id.rank);
        rankTitle = (TextView)findViewById(R.id.rank_title);
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);

        setToolbar();
        setBottomView();
        setInfoBanner();

        calendarView.setType(getIntent().getIntExtra("type",calendarView.getType()));
        calendarView.setYear(getIntent().getIntExtra("year",calendarView.getYear()));
        calendarView.setMonth(getIntent().getIntExtra("month",calendarView.getMonth()));
        calendarView.setQuarter(getIntent().getIntExtra("quarter",calendarView.getQuarter()));

        setTabBar();

        calendarView.setCalendarClickListener(new CalendarView.OnCalendarClickListener() {
            @Override
            public void onBackwardClick() {
                Log.d("debug", "act back");
                getData();
            }

            @Override
            public void onForwardClick() {
                Log.d("debug", "act for");
                getData();
            }
        });

//        rankTitle.setText("广州天河路珠宝店" + getString(R.string.rank));
//        rankText.setText("5");

        setViewPager();
    }

    private void getData(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getData();
            }
        };

        if (getDataTask != null)
            getDataTask.cancel();

        progress.setVisibility(View.VISIBLE);
        final Call<BaseResponse> getDataSubTask = apiService.getMyTargetPageTask("Bearer " + SharedPreference.getToken(this),calendarView.getStartDate(), calendarView.getEndDate(), user.id,"percentage");
        getDataTask = getDataSubTask;

        getDataSubTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getDataSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        MyTargetPageResponse temp = new Gson().fromJson(data, MyTargetPageResponse.class);
                        UserTargetData userTargetData = new UserTargetData(temp.user_targets, calendarView.getStartDate());
                        SalesData salesData = new SalesData(temp.user_sales, calendarView.getStartDate());
                        setData(userTargetData, salesData, temp.getRank(),getIntent().getStringExtra("short_des") + getString(R.string.rank));
//                        userPlaceText.setText(temp.unit);
                    } else {
                        SharedUtils.handleServerError(TypeAInOtherActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getDataSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetry(TypeAInOtherActivity.this, retry);
                }
            }
        });
    }


    private void setViewPager(){
        fragments = new ArrayList<>();

        fragments.add(TargetTypeBCFragment.newInstance(null,null, 0, getString(R.string.target_type_all), ""));
        fragments.add(TargetTypeBCFragment.newInstance(null,null, 0, getString(R.string.target_type_A), ""));
        fragments.add(TargetTypeBCFragment.newInstance(null,null, 0, getString(R.string.target_type_F), ""));
        fragments.add(TargetTypeBCFragment.newInstance(null,null, 0, getString(R.string.target_type_E), ""));
        if (ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_HK)) {
            fragments.add(TargetTypeBCFragment.newInstance(null,null, 0, getString(R.string.target_type_M), ""));
        }

            PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((TargetTypeBCFragment)fragments.get(position)).startCircleViewAnimation();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //preset Data To Fragment
        final UserTargetData userTargetData = new UserTargetData(user.user_targets, calendarView.getStartDate());
        final SalesData salesData = new SalesData(user.user_sales, calendarView.getStartDate());
        setData(userTargetData, salesData, new Ranks(),currentUser.getLongDepartmentName());
        circleIndicator.setViewPager(viewPager);
    }

    private void setData(final UserTargetData userTargetData , final SalesData salesData, final Ranks rank, final String rankDes){
        for (int i = 0 ; i< fragments.size(); i++){
            final TargetTypeBCFragment fragment = ((TargetTypeBCFragment) fragments.get(i));
            if (i ==0) {
//                CircleData data = new CircleData(getString(R.string.sales_target), SharedUtils.addCommaToNum(userTargetData.getBaseAll()),CircleData.TYPE_MONEY);
//                fragment.putData(data, null,userTargetData.getAllRate(salesData), rank.getAll(), rankDes);
//                fragment.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                        CircleData data1 = new CircleData(getString(R.string.breakdown_target),SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()),CircleData.TYPE_MONEY);
//                        CircleData data2 = new CircleData(getString(R.string.breakdown_target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getDistributedAllRate(salesData),"%"),CircleData.TYPE_TEXT);
//                        blurView.setLocation(fragment.getCircleViewLocation());
//                        blurView.setData(data1,data2);
//                        blurView.show();
//                    }
//                });

                CircleData data = new CircleData(getString(R.string.breakdown_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()),CircleData.TYPE_MONEY);
                fragment.putData(data, null,userTargetData.getDistributedAllRate(salesData), rank.getAll(), rankDes);
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.sales_target),SharedUtils.addCommaToNum(userTargetData.getBaseAll()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(getString(R.string.target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getAllRate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.setData(data1,data2);
                        blurView.show();
                    }
                });
            }
            else if (i ==1){
//                CircleData data = new CircleData(getString(R.string.sales_target), SharedUtils.addCommaToNum(userTargetData.getBaseA()),CircleData.TYPE_MONEY);
//                fragment.putData(data, null,userTargetData.getARate(salesData), rank.getA(), rankDes);
//                fragment.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                        CircleData data1 = new CircleData(getString(R.string.breakdown_target),SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_A()),CircleData.TYPE_MONEY);
//                        CircleData data2 = new CircleData(getString(R.string.breakdown_target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getDistributedARate(salesData),"%"),CircleData.TYPE_TEXT);
//                        blurView.setLocation(fragment.getCircleViewLocation());
//                        blurView.setData(data1,data2);
//                        blurView.show();
//                    }
//                });
                CircleData data = new CircleData(getString(R.string.breakdown_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_A()),CircleData.TYPE_MONEY);
                fragment.putData(data, null,userTargetData.getDistributedARate(salesData), rank.getAll(), rankDes);
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.sales_target),SharedUtils.addCommaToNum(userTargetData.getBaseA()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(getString(R.string.target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getARate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.setData(data1,data2);
                        blurView.show();
                    }
                });
            }
            else if (i ==2){
//                CircleData data = new CircleData(getString(R.string.sales_target), SharedUtils.addCommaToNum(userTargetData.getBaseF()),CircleData.TYPE_MONEY);
//                fragment.putData(data, null,userTargetData.getFRate(salesData), rank.getF(), rankDes);
//                fragment.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                        CircleData data1 = new CircleData(getString(R.string.breakdown_target),SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_F()),CircleData.TYPE_MONEY);
//                        CircleData data2 = new CircleData(getString(R.string.breakdown_target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getDistributedFRate(salesData),"%"),CircleData.TYPE_TEXT);
//                        blurView.setLocation(fragment.getCircleViewLocation());
//                        blurView.setData(data1,data2);
//                        blurView.show();
//                    }
//                });
                CircleData data = new CircleData(getString(R.string.breakdown_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_F()),CircleData.TYPE_MONEY);
                fragment.putData(data, null,userTargetData.getDistributedFRate(salesData), rank.getAll(), rankDes);
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.sales_target),SharedUtils.addCommaToNum(userTargetData.getBaseF()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(getString(R.string.target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getFRate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.setData(data1,data2);
                        blurView.show();
                    }
                });
            }
            else if (i==3){
//                CircleData data = new CircleData(getString(R.string.sales_target), SharedUtils.addCommaToNum(userTargetData.getBaseE()),CircleData.TYPE_MONEY);
//                fragment.putData(data, null,userTargetData.getERate(salesData), rank.getE(), rankDes);
//                fragment.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                        CircleData data1 = new CircleData(getString(R.string.breakdown_target),SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_E()),CircleData.TYPE_MONEY);
//                        CircleData data2 = new CircleData(getString(R.string.breakdown_target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getDistributedERate(salesData),"%"),CircleData.TYPE_TEXT);
//                        blurView.setLocation(fragment.getCircleViewLocation());
//                        blurView.setData(data1,data2);
//                        blurView.show();
//                    }
//                });
                CircleData data = new CircleData(getString(R.string.breakdown_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_E()),CircleData.TYPE_MONEY);
                fragment.putData(data, null,userTargetData.getDistributedERate(salesData), rank.getAll(), rankDes);
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.sales_target),SharedUtils.addCommaToNum(userTargetData.getBaseE()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(getString(R.string.target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getERate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.setData(data1,data2);
                        blurView.show();
                    }
                });
            }
            else if (i==4){
//                CircleData data = new CircleData(getString(R.string.sales_target), SharedUtils.addCommaToNum(userTargetData.getBaseM()),CircleData.TYPE_MONEY);
//                fragment.putData(data, null,userTargetData.getMRate(salesData), rank.getM(), rankDes);
//                fragment.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                        CircleData data1 = new CircleData(getString(R.string.breakdown_target),SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_M()),CircleData.TYPE_MONEY);
//                        CircleData data2 = new CircleData(getString(R.string.breakdown_target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getDistributedMRate(salesData),"%"),CircleData.TYPE_TEXT);
//                        blurView.setLocation(fragment.getCircleViewLocation());
//                        blurView.setData(data1,data2);
//                        blurView.show();
//                    }
//                });
                CircleData data = new CircleData(getString(R.string.breakdown_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_M()),CircleData.TYPE_MONEY);
                fragment.putData(data, null,userTargetData.getDistributedMRate(salesData), rank.getAll(), rankDes);
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.sales_target),SharedUtils.addCommaToNum(userTargetData.getBaseM()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(getString(R.string.target_complete_rate), SharedUtils.addCommaToNum(userTargetData.getMRate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.setData(data1,data2);
                        blurView.show();
                    }
                });
            }
        }
    }

    private void setTabBar(){
        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        tabEntities.add(new TabEntity(getString(R.string.monthly)));
        tabEntities.add(new TabEntity(getString(R.string.quarterly)));
        tabEntities.add(new TabEntity(getString(R.string.yearly)));


        tabLayout.setTabData(tabEntities);

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0){
                    calendarView.setType(CalendarView.TYPE_MONTH);
                }
                else if (position == 1){
                    calendarView.setType(CalendarView.TYPE_QUARTER);
                }
                else if (position == 2){
                    calendarView.setType(CalendarView.TYPE_YEAR);
                }
                getData();
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        tabLayout.setCurrentTab(getIntent().getIntExtra("type",calendarView.getType()));

        tabLayout.setVisibility(View.VISIBLE);
        upperLine.setVisibility(View.VISIBLE);
        lowerLine.setVisibility(View.VISIBLE);
    }

    private void setInfoBanner(){
            //set profile pic
        Glide.with(this).load(user.getIconUrl()).placeholder(R.drawable.user_placeholder).dontAnimate().into(profileImage);
        userNameText.setText(user.name);
        userPositionText.setText(user.title);
        userPlaceText.setText(getIntent().getStringExtra("long_des"));
    }




    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        ImageView doneBtn = (ImageView)findViewById(R.id.done_btn);

        toolbarTitle.setText(R.string.team_target);
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.VISIBLE);
        doneBtn.setVisibility(View.GONE);
    }

    private void setBottomView(){
        BottomView bottomView = (BottomView) findViewById(R.id.bottom_view);
        final MenuView menuView = (MenuView) findViewById(R.id.menu_view);
        bottomView.setMenuView(menuView);
    }


    @Override
    protected void onDestroy() {
        if (getDataTask != null){
            getDataTask.cancel();
        }
        super.onDestroy();
    }
}
