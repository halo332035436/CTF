package com.bullb.ctf.PerformanceEnquiry;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.PerformanceEnquiry.TypeA.PerformanceEnquiryAFragment;
import com.bullb.ctf.PerformanceEnquiry.TypeB.PerformanceEnquiryBFragment;
import com.bullb.ctf.PerformanceEnquiry.TypeC.PerformanceEnquiryCFragment;
import com.bullb.ctf.PerformanceEnquiry.TypeSummary.PerformanceEnquirySummaryFragment;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.WidgetUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PerformanceEnquiryActivity extends AppCompatActivity  implements View.OnClickListener{
    public User user;
    public CalendarView calendarView;
    private TextView userNameText, userPositionText, userPlaceText, teamLocationText;
    private SelectableRoundedImageView profileImage;
    private LinearLayout personalInfoLayout, teamInfoLayout;
    private ImageView breakDownBtn;
    private CommonTabLayout tabLayout;
    private View upperLine, lowerLine;
    private String userType;
    private FragmentManager fm;
    private Call<BaseResponse> recordPageTask;
    private KeyTools keyTools;
    private ApiService apiService;
    public String longDescription;
//    public String shortDescription;
    private Department department;
    private ImageView layerBtn;

    private static final int SELECT_LAYER_CODE = 1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_enquiry);
        apiService = ServiceGenerator.createService(ApiService.class, this);
        keyTools = KeyTools.getInstance(this);

        user = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        if (user == null) {
            user = SharedPreference.getUser(this);
        }

        department = new Gson().fromJson(getIntent().getStringExtra("department"), Department.class);
        if (SharedPreference.getUser(this).equals(User.USER_TYPE_C) && department == null) {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
            finish();
        }

        userType = getIntent().getStringExtra("view_type");
        if (userType== null || userType.isEmpty()){
            userType = user.type;
        }

        longDescription = getIntent().getStringExtra("long_des");
        if (longDescription == null){
            if (department != null){
                longDescription = department.getLong_description();
            }
            else {
                longDescription = user.getLongDepartmentName();
            }
        }

//        shortDescription = getIntent().getStringExtra("short_des");
//        if (shortDescription == null){
//            if (department != null){
//                shortDescription = department.getShort_description();
//            }
//            else {
//                shortDescription = user.getShortDepartmentName();
//            }
//        }


        initUi();
        recordPage("sale");

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
        personalInfoLayout = (LinearLayout)findViewById(R.id.personal_info_banner_layout);
        teamInfoLayout = (LinearLayout)findViewById(R.id.team_info_banner_layout);
        teamLocationText = (TextView)findViewById(R.id.team_location);
        breakDownBtn = (ImageView)findViewById(R.id.breakdown_btn);
        tabLayout = (CommonTabLayout)findViewById(R.id.period_tablayout);
        upperLine = findViewById(R.id.upper_tab_line);
        lowerLine = findViewById(R.id.lower_tab_line);

        layerBtn = (ImageView)findViewById(R.id.layer_btn);

        setToolbar();
        setBottomView();
        setInfoBanner();
        setTabBar();

        calendarView.setType(getIntent().getIntExtra("type",calendarView.getType()));
        calendarView.setYear(getIntent().getIntExtra("year",calendarView.getYear()));
        calendarView.setMonth(getIntent().getIntExtra("month",calendarView.getMonth()));
        calendarView.setQuarter(getIntent().getIntExtra("quarter",calendarView.getQuarter()));

        calendarView.setCalendarClickListener(new CalendarView.OnCalendarClickListener() {
            @Override
            public void onBackwardClick() {
                Log.d("debug", "act back");
            }

            @Override
            public void onForwardClick() {
                Log.d("debug", "act for");

            }
        });

        boolean singleViewable = user.getViewable_root_departments().size()<=1;
//        if(user.getViewable_root_departments().size()>1){
//            layerBtn.setVisibility(View.VISIBLE);
//            layerBtn.setOnClickListener(this);
//        }else{
//            layerBtn.setVisibility(View.GONE);
//            singleViewable = true;
//        }


        layerBtn.setVisibility(View.VISIBLE);
        layerBtn.setOnClickListener(this);

        fm = getSupportFragmentManager();

        if(getIntent().getBooleanExtra("summary",true) && !singleViewable){
            teamLocationText.setText(R.string.summary);
            PerformanceEnquirySummaryFragment summaryFragment = PerformanceEnquirySummaryFragment.newInstance(userType,calendarView.getType());
            fm.beginTransaction().replace(R.id.performace_enquiry_frame, summaryFragment).commit();
            summaryFragment.presetData(department);
        }else {
            if (userType.equals(User.USER_TYPE_A)) {
                PerformanceEnquiryAFragment fragment = PerformanceEnquiryAFragment.newInstance(userType, "");
                fm.beginTransaction().replace(R.id.performace_enquiry_frame, fragment).commit();

                //presetData
                final UserTargetData userTargetData = new Gson().fromJson(getIntent().getStringExtra("target_data"), UserTargetData.class);
                final SalesData salesData = new Gson().fromJson(getIntent().getStringExtra("sales_data"), SalesData.class);
                fragment.presetData(userTargetData, salesData, user, longDescription, longDescription);
            } else if (userType.equals(User.USER_TYPE_B)) {
                PerformanceEnquiryBFragment fragment = PerformanceEnquiryBFragment.newInstance(userType, calendarView.getType());
                fm.beginTransaction().replace(R.id.performace_enquiry_frame, fragment).commit();
                fragment.presetData(department);

            } else if (userType.equals(User.USER_TYPE_C)) {
                PerformanceEnquiryCFragment fragment = PerformanceEnquiryCFragment.newInstance(userType, calendarView.getType());
                fm.beginTransaction().replace(R.id.performace_enquiry_frame, fragment).commit();
                fragment.presetData(department);

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
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        int tabType = getIntent().getIntExtra("type",calendarView.getType());

        tabLayout.setCurrentTab(tabType);

        tabLayout.setVisibility(View.VISIBLE);
        upperLine.setVisibility(View.VISIBLE);
        lowerLine.setVisibility(View.VISIBLE);
    }


    private void setInfoBanner(){
        if (userType.equals(User.USER_TYPE_A)){
            personalInfoLayout.setVisibility(View.VISIBLE);
            teamInfoLayout.setVisibility(View.GONE);

            //set profile pic
            Glide.with(this).load(user.getIconUrl()).placeholder(R.drawable.user_placeholder).dontAnimate().into(profileImage);

            userNameText.setText(user.name);
            userPositionText.setText(user.title);
            userPlaceText.setText(longDescription);
        }
        else if (userType.equals(User.USER_TYPE_B)){
            personalInfoLayout.setVisibility(View.GONE);
            teamInfoLayout.setVisibility(View.VISIBLE);
            breakDownBtn.setVisibility(View.GONE);
            teamLocationText.setText(longDescription);
        }
        else if (userType.equals(User.USER_TYPE_C)){
            personalInfoLayout.setVisibility(View.GONE);
            teamInfoLayout.setVisibility(View.VISIBLE);
            if (department == null)
                teamLocationText.setText(user.getLongDepartmentName());
            else
                teamLocationText.setText(department.getLong_description());
            breakDownBtn.setVisibility(View.GONE);
        }
    }


    private void recordPage(String page){
        Map<String, String> dataMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data json", jsonObject.toString());
        dataMap = keyTools.encrypt(jsonObject.toString());

        recordPageTask = apiService.recordAccessTask("Bearer " + SharedPreference.getToken(PerformanceEnquiryActivity.this), dataMap);
        recordPageTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
            }
        });
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        if (user.type.equals(User.USER_TYPE_A)) {
            toolbarTitle.setText(R.string.my_performance);
        }
        else{
            toolbarTitle.setText(R.string.team_performance);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layer_btn:
                Intent layerIntent = new Intent();
                layerIntent.setClass(this, PerformanceEnquirySelectActivity.class);
                startActivityForResult(layerIntent,SELECT_LAYER_CODE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_LAYER_CODE && resultCode==RESULT_OK){
            finish();
        }
    }
}
