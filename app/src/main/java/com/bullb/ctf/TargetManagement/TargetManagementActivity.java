package com.bullb.ctf.TargetManagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.CheckDateResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.BonusesData;
import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.KeyValue;
import com.bullb.ctf.Model.RewardData;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.SelfManagementData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserRole;
import com.bullb.ctf.Model.UserTarget;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.TargetManagement.BreakDown.BreakDownActivity;
import com.bullb.ctf.Utils.Date;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.CalendarView;
import com.bullb.ctf.Widget.MenuView;
import com.bullb.ctf.Widget.TabEntity;
import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.google.gson.Gson;
import com.joooonho.SelectableRoundedImageView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TargetManagementActivity extends AppCompatActivity implements View.OnClickListener, TargetManagementFragment.OnDateChangeListener {
    private CalendarView calendarView;
    private User user;
    private TextView userNameText, userPositionText, userPlaceText, teamLocationText;
    private SelectableRoundedImageView profileImage;
    private FragmentManager fm;
    private LinearLayout personalInfoLayout, teamInfoLayout;
    private ImageView breakDownBtn;
    private CommonTabLayout tabLayout;
    private View upperLine, lowerLine;
    private String userType;
    private ApiService apiService;
    private Call<BaseResponse> recordPageTask;
    private Call<BaseResponse> confirmTargetTask;
    private Call<BaseResponse> getSubmitAvailableTask;
    private KeyTools keyTools;
    private Department department;
    private Button confirmBtn;
    private ImageView layerBtn;
    private AVLoadingIndicatorView progress;
    private Call<BaseResponse> getSelfManagementTask;

    private static final int SELECT_LAYER_CODE = 1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_management);
        apiService = ServiceGenerator.createService(ApiService.class, this);
        keyTools = KeyTools.getInstance(this);

        user = SharedPreference.getUser(this);
        userType = getIntent().getStringExtra("view_type");
        if (userType== null || userType.isEmpty()){
            userType = user.type;
        }
        if (userType.equals(User.USER_TYPE_B) || userType.equals(User.USER_TYPE_C)){
            department = new Gson().fromJson(getIntent().getStringExtra("department"), Department.class);
        }

        initUi();

        recordPage("target");
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

        confirmBtn = (Button)findViewById(R.id.confirm_btn);

        setConfirmAvailable(false);
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);
        setToolbar();
        setBottomView();
        setInfoBanner();
        if (!userType.equals(User.USER_TYPE_A)) {
            setTabBar();
        }else{

            getSelfManagement();


//            if(!targetA.confirmed && (dayOfMonth >= 2 && dayOfMonth <= 6)) {
//                confirmBtn.setBackgroundResource(R.drawable.red_btn_bg);
//            }else{
//                confirmBtn.setBackgroundResource(R.drawable.grey_btn_bg);
//                confirmBtn.setOnClickListener(null);
//            }
        }

        int year = getIntent().getIntExtra("year", calendarView.getYear());
        int month = getIntent().getIntExtra("month", calendarView.getMonth());
        int quarter = getIntent().getIntExtra("quarter", calendarView.getQuarter());
        int type  = getIntent().getIntExtra("type", calendarView.getType());

        calendarView.setType(type);
        calendarView.setYear(year);
        calendarView.setMonth(month);
        calendarView.setQuarter(quarter);


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

        //set Fragment
        fm = getSupportFragmentManager();

        if(getIntent().getBooleanExtra("summary",true) && !singleViewable){
            teamLocationText.setText(R.string.summary);
            SummaryTargetManagementFragment summaryFragment = SummaryTargetManagementFragment.newInstance(userType,"");
            fm.beginTransaction().replace(R.id.target_frame, summaryFragment).commit();
        }else{
            TargetManagementFragment fragment = TargetManagementFragment.newInstance(userType,"");
            fm.beginTransaction().replace(R.id.target_frame, fragment).commit();
        }


    }

    private boolean allUnconfirmed(UserTargetData userTargetData){
//        return true;
        try {
            UserTarget targetA = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_A, SharedUtils.getStartDate(Calendar.getInstance()));
            UserTarget targetE = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_E, SharedUtils.getStartDate(Calendar.getInstance()));
            UserTarget targetF = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_F, SharedUtils.getStartDate(Calendar.getInstance()));
            return (targetA.confirmed == null || !targetA.confirmed) && (targetE.confirmed == null || !targetE.confirmed) && (targetF.confirmed == null || !targetF.confirmed);
        }catch (NullPointerException e){
            return false;
        }
    }
    private void setConfirmBtn(){

        final UserTargetData userTargetData = SharedPreference.getTarget(this);
//        final UserTarget targetA = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_A, SharedUtils.getStartDate(Calendar.getInstance()));


        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Date date = new Date();

        progress.setVisibility(View.VISIBLE);
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setConfirmBtn();
            }
        };
        getSubmitAvailableTask = apiService.getSubmitAvailable("Bearer "+SharedPreference.getToken(this), date.getFromDate(Date.TYPE_MONTH));
        getSubmitAvailableTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getSubmitAvailableTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        final CheckDateResponse checkDateResponse = new Gson().fromJson(data, CheckDateResponse.class);

                        if( allUnconfirmed(userTargetData) && checkDateResponse.confirmable) {
                            setConfirmAvailable(true);
                        }else{
                            setConfirmAvailable(false);
                        }
                    } else {
                        setConfirmAvailable(false);
                        SharedUtils.handleServerError(getApplicationContext(), response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getSubmitAvailableTask.isCanceled()) {
                    setConfirmAvailable(false);
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetry(TargetManagementActivity.this, retry);
                }
            }
        });
    }

    private void setConfirmAvailable(boolean available){
        if(available){

            confirmBtn.setBackgroundResource(R.drawable.red_btn_bg);
            confirmBtn.setOnClickListener(this);
        }else {
            confirmBtn.setBackgroundResource(R.drawable.grey_btn_bg);
            confirmBtn.setOnClickListener(null);
        }
    }

    private void setTabBar(){
        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        tabEntities.add(new TabEntity(getString(R.string.monthly)));
        tabEntities.add(new TabEntity(getString(R.string.quarterly)));
        tabEntities.add(new TabEntity(getString(R.string.yearly)));

        tabLayout.setTabData(tabEntities);
        tabLayout.setCurrentTab(getIntent().getIntExtra("type", calendarView.getType()));

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
            userPlaceText.setText(user.getLongDepartmentName());
        }
        else if (userType.equals(User.USER_TYPE_B)){
            personalInfoLayout.setVisibility(View.GONE);
            teamInfoLayout.setVisibility(View.VISIBLE);
            if (department != null){
                teamLocationText.setText(department.long_description);
            } else {
                teamLocationText.setText(user.getLongDepartmentName());
            }
            breakDownBtn.setOnClickListener(this);
        }
        else if (userType.equals(User.USER_TYPE_C)){
            personalInfoLayout.setVisibility(View.GONE);
            teamInfoLayout.setVisibility(View.VISIBLE);
            if (department != null){
                teamLocationText.setText(department.long_description);
            } else {
                teamLocationText.setText(user.getLongDepartmentName());
            }
               breakDownBtn.setVisibility(View.GONE);
        }


        UserRole userRole = user.getUserRole();
        ArrayList<KeyValue> keyValues = new ArrayList<>();
        boolean hasSplitPermission = false;
        if (userRole != null)
            keyValues = userRole.getDetail();

        if (keyValues != null) {
            for (KeyValue keyValue : keyValues) {
                if(keyValue.getKey().equals("indicator_split")){
                    if(keyValue.getValue()){
                        hasSplitPermission = true;
                    }
                }
            }
        }

        if(department != null) {
//            if (user.type.equals(User.USER_TYPE_B) && department.id.equals(user.getDepartment().id)) {
            if (user.type.equals(User.USER_TYPE_B) && department.manager_id!=null &&
                    ( department.manager_id.equals(user.id) || ( user.department_id.equals(department.id) && hasSplitPermission ))
            ){
                breakDownBtn.setVisibility(View.VISIBLE);
            } else {
                breakDownBtn.setVisibility(View.GONE);
            }
        }else{
            breakDownBtn.setVisibility(View.GONE);
        }
    }

    private void showConfirmDialog(){
        new android.app.AlertDialog.Builder(this)
                .setMessage(R.string.sure_confirm_target)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmTarget();
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void confirmTarget(){

        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmTarget();
            }
        };

        UserTargetData userTargetData = SharedPreference.getTarget(this);
        UserTarget targetA = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_A, SharedUtils.getStartDate(Calendar.getInstance()));
        UserTarget targetF = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_F, SharedUtils.getStartDate(Calendar.getInstance()));
        UserTarget targetE = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_E, SharedUtils.getStartDate(Calendar.getInstance()));
        JSONArray jsonArrays = new JSONArray();

        jsonArrays.put(targetA.id);
        jsonArrays.put(targetE.id);
        jsonArrays.put(targetF.id);

        Map<String, String> dataMap = new HashMap<>();
        dataMap = keyTools.encrypt(jsonArrays.toString());
        progress.setVisibility(View.VISIBLE);

        confirmTargetTask = apiService.confirmTarget("Bearer "+SharedPreference.getToken(this), dataMap);
        confirmTargetTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!confirmTargetTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), R.string.target_confirmed, Toast.LENGTH_SHORT).show();
//                        finish();

                        setConfirmAvailable(false);

                        getSelfManagement();
                    } else {
                        SharedUtils.handleServerError(getApplicationContext(), response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!confirmTargetTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetry(TargetManagementActivity.this, retry);
                }
            }
        });
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

        recordPageTask = apiService.recordAccessTask("Bearer " + SharedPreference.getToken(TargetManagementActivity.this), dataMap);
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
    }

    private void setBottomView(){
        BottomView bottomView = (BottomView) findViewById(R.id.bottom_view);
        final MenuView menuView = (MenuView) findViewById(R.id.menu_view);
        bottomView.setMenuView(menuView);
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        if(fragment instanceof TargetManagementFragment){
            TargetManagementFragment targetManagementFragment = (TargetManagementFragment)fragment;
            targetManagementFragment.setOnDateChangeListener(this);
        }
    }

    @Override
    public void onDateChange(String fromDate, String toDate) {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int current = Integer.parseInt(fromDate.substring(5,7));
        if(++month == current){
            confirmBtn.setVisibility(View.VISIBLE);
        }else{
            confirmBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.breakdown_btn:
                Intent intent = new Intent();
                intent.setClass(this, BreakDownActivity.class);
                startActivity(intent);
                Log.d("debug", "breakdown");
                break;
            case R.id.layer_btn:
                Intent layerIntent = new Intent();
                layerIntent.setClass(this, TargetManagementSelectActivity.class);
                startActivityForResult(layerIntent,SELECT_LAYER_CODE);
                break;
            case R.id.confirm_btn:
                showConfirmDialog();
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

    private void getSelfManagement(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSelfManagement();
            }
        };

        if(SharedUtils.serverIsHongKong(this)) {
            confirmBtn.setVisibility(View.GONE);
            return;
        }

        progress.setVisibility(View.VISIBLE);
        getSelfManagementTask = apiService.getSelfManagementTask("Bearer " + SharedPreference.getToken(this));
        getSelfManagementTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv, response.body().data);
                    Gson gson = new Gson();
                    SelfManagementData selfManagementData = gson.fromJson(data, SelfManagementData.class);

                    UserTargetData userTargetData = new UserTargetData(selfManagementData.user_targets, selfManagementData.target, selfManagementData.decomposed_target, SharedUtils.getFirstDay(Calendar.getInstance()));
                    //store temp data in shared preference for reuse
                    SharedPreference.setTarget(userTargetData, TargetManagementActivity.this);
                    setConfirmBtn();
                }
                else{

                    setConfirmAvailable(false);
                    SharedUtils.handleServerError(TargetManagementActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                SharedUtils.networkErrorDialogWithRetry(TargetManagementActivity.this, retry);
            }
        });
    }

}
