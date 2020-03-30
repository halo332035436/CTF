package com.bullb.ctf.TargetManagement.BreakDown;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.CheckDateResponse;
import com.bullb.ctf.API.Response.TeamTargetPageResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.DepartmentTargetData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserTarget;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.Date;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.MenuView;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BreakDownActivity extends AppCompatActivity implements View.OnClickListener, ObservableScrollViewCallbacks {
    private BreakDownViewAdapter adapter;
    private ObservableRecyclerView recyclerView;
    private ArrayList<User> dataList;
    private ApiService apiService;
    private Call<BaseResponse> getTeamPageTask;
    private Call<BaseResponse> updateTargetTask;
    private KeyTools keyTools;
    private AVLoadingIndicatorView progress;
    private Calendar cal;
    private Button submitBtn;

    private Call<BaseResponse> getSubmitAvailableTask;
    private boolean checkedCurrent;

    private TeamTargetPageResponse teamResponse;

    public static int CN_EDIT_BREAKDOWN = 1241;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_down);
        apiService = ServiceGenerator.createService(ApiService.class, this);
        keyTools = KeyTools.getInstance(this);
        initUi();

        getTeamData();
    }

    private void getTeamData(){
        cal = Calendar.getInstance();
//        if(!BuildConfig.DEBUG) {
        if (ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_CN)) {
            Date date = new Date();
            checkedCurrent = false;
            getMonthData(date);
        }

        if(ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_HK)) {
            getTeamPage(getStartDate(cal), getEndData(cal), null, null);
        }

    }

    private void getMonthData(final Date date){

        progress.setVisibility(View.VISIBLE);
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getMonthData(date);
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
                        cal = Calendar.getInstance();
                        if(checkedCurrent){
                            if(!checkDateResponse.updateable){
                                submitBtn.setBackgroundResource(R.drawable.grey_btn_bg);
                                submitBtn.setOnClickListener(null);
                            }
                        }else{
                            checkedCurrent = true;
                            if(!checkDateResponse.updateable){
                                cal.add(Calendar.MONTH, 1);
                                getTeamPage(getStartDate(cal), getEndData(cal), null, null);
                                getMonthData(new Date().getAddedMonth());
                                setToolbarTitle(true);

                            }else {
                                getTeamPage(getStartDate(cal), getEndData(cal), null, null);
                                setToolbarTitle(false);
                            }
                        }


                    } else {
                        SharedUtils.handleServerError(getApplicationContext(), response);
                        submitBtn.setBackgroundResource(R.drawable.grey_btn_bg);
                        submitBtn.setOnClickListener(null);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getSubmitAvailableTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    submitBtn.setBackgroundResource(R.drawable.grey_btn_bg);
                    submitBtn.setOnClickListener(null);
                    SharedUtils.networkErrorDialogWithRetry(BreakDownActivity.this, retry);
                }
            }
        });
    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        recyclerView = (ObservableRecyclerView)findViewById(R.id.breakdown_recyclerview);
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);
        submitBtn = (Button)findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitNewTarget();
            }
        });
        setToolbar();
        setBottomView();



        dataList = new ArrayList<>();

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        adapter = new BreakDownViewAdapter(this, dataList, mLayoutManager);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setScrollViewCallbacks(this);

        if(SharedUtils.serverIsHongKong(this)){
            submitBtn.setVisibility(View.GONE);
        }
    }

    private String getEndData(Calendar cal){
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH)+1);
        //TODO month +1

        String lastDay = String.valueOf(cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        if (lastDay.length() < 2) {
            lastDay = "0" + lastDay;
        }

        if (month.length() < 2) {
            month = "0" + month;
        }
        return year + "-" + month + "-" + lastDay;
    }

    private String getStartDate(Calendar cal) {
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH)+1);

        if (month.length() < 2) {
                month = "0" + month;
            }
        return year + "-" + month + "-01";
    }



    private void getTeamPage(final String from_date, final String to_date, final String mode, final String department_id){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTeamPage(from_date, to_date, mode , department_id);
            }
        };

        if (getTeamPageTask != null)
            getTeamPageTask.cancel();

        progress.setVisibility(View.VISIBLE);
        final Call<BaseResponse> getTeamPageSubTask = apiService.getTeamTargetTask("Bearer " + SharedPreference.getToken(this), from_date, to_date, mode, department_id, "percentage", 1);
        getTeamPageTask = getTeamPageSubTask;

        getTeamPageSubTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getTeamPageSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        TeamTargetPageResponse temp = new Gson().fromJson(data, TeamTargetPageResponse.class);
                        teamResponse = temp;
                        setData(temp);
                    } else {
                        SharedUtils.handleServerError(BreakDownActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getTeamPageSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetry(BreakDownActivity.this, retry);
                }
            }
        });
    }

    private void setData(TeamTargetPageResponse response){
        dataList.clear();
        if (response.users != null)
            dataList.addAll(response.users);

        DepartmentTargetData departmentTargetData = new DepartmentTargetData(response.department_targets, dataList, getStartDate(cal));
        adapter.setData(departmentTargetData, getStartDate(cal));
    }

    private void setToolbarTitle(boolean isNextMonth){
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        if(isNextMonth) {
            toolbarTitle.setText(SharedUtils.getNextMonthText(this) + getString(R.string.breakdown_title));
        }else{
            toolbarTitle.setText(SharedUtils.getThisMonthText(this) + getString(R.string.breakdown_title));
        }
    }


    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        ImageView doneBtn = (ImageView)findViewById(R.id.done_btn);

        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//        if(ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_CN) && dayOfMonth>1) {
//            toolbarTitle.setText(SharedUtils.getNextMonthText(this) + getString(R.string.breakdown_title));
//        }else{
//            toolbarTitle.setText(SharedUtils.getThisMonthText(this) + getString(R.string.breakdown_title));
//        }
        if(ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_HK)) {
            toolbarTitle.setText(SharedUtils.getThisMonthText(this) + getString(R.string.breakdown_title));
        }
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.GONE);
        doneBtn.setVisibility(View.GONE);
    }

    private void updateUser(String id, Intent data){
        Calendar caltemp = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String now = format1.format(caltemp.getTime());

        int index = -1;
        for(int i=0;i<dataList.size();i++){
            User user = dataList.get(i);
            if(user.id.equals(id)){
                index = i;
                Log.d("TargetTest", "before: " + user.name);
                for(UserTarget target: user.user_targets){
                    try {
                        if(SharedUtils.isSameYear(now,target.from_date)) {
//                            Log.d("TargetTest", "before: " + target.type + " -> "+target.manager_addition);
                            switch (target.type) {
                                case UserTarget.TYPE_A:
                                    target.manager_addition = data.getDoubleExtra("adjust_a",0.0);
                                    break;
                                case UserTarget.TYPE_E:
                                    target.manager_addition = data.getDoubleExtra("adjust_e",0.0);
                                    break;
                                case UserTarget.TYPE_F:
                                    target.manager_addition = data.getDoubleExtra("adjust_f",0.0);
                                    break;
                                default:
                                    break;
                            }
                            Log.d("TargetTest", "after: " + target.type + " -> "+target.manager_addition);

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if(index >= 0) {
            adapter.notifyItemChanged(index+1);
        }

    }

    private void submitNewTarget(){

            UserTargetData userTargetData;
            JSONArray jsonArrays = new JSONArray();
            for(User user:dataList) {
                userTargetData = new UserTargetData(user.user_targets, getStartDate(Calendar.getInstance()));


                UserTarget targetA = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_A, getStartDate(Calendar.getInstance()));
                UserTarget targetF = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_F, getStartDate(Calendar.getInstance()));
                UserTarget targetE = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_E, getStartDate(Calendar.getInstance()));
                if (targetA == null || targetF == null || targetE == null) {
                    if (ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_CN)) {
//                        Toast.makeText(this, R.string.error_no_next_target, Toast.LENGTH_SHORT).show();

                    } else {
//                        Toast.makeText(this, R.string.error_no_current_target, Toast.LENGTH_SHORT).show();

                    }
                    continue;
                }


                int precision = 4;
                JSONObject objectA = new JSONObject();
                JSONObject objectF = new JSONObject();
                JSONObject objectE = new JSONObject();
                try {
                    objectA.put("manager_addition", targetA.manager_addition);
                    objectF.put("manager_addition", targetF.manager_addition);
                    objectE.put("manager_addition", targetE.manager_addition);

//                    objectA.put("manager_addition", SharedUtils.round(targetA.manager_addition,precision));
                    objectA.put("id", targetA.id);
//                    objectF.put("manager_addition", SharedUtils.round(targetF.manager_addition,precision));
                    objectF.put("id", targetF.id);
//                    objectE.put("manager_addition", SharedUtils.round(targetE.manager_addition,precision));
                    objectE.put("id", targetE.id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArrays.put(objectA);
                jsonArrays.put(objectE);
                jsonArrays.put(objectF);
            }

            final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    submitNewTarget();
                }
            };

            Map<String, String> dataMap = new HashMap<>();
            JSONObject jsonObject = new JSONObject();

            dataMap = keyTools.encrypt(jsonArrays.toString());

            progress.setVisibility(View.VISIBLE);

            if(!SharedUtils.serverIsHongKong(this)) {

                Log.d("TestTarget", "submitNewTarget: "+jsonArrays.toString());
                updateTargetTask = apiService.updateTargetTask("Bearer " + SharedPreference.getToken(this), dataMap);
                updateTargetTask.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (!updateTargetTask.isCanceled()) {
                            progress.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), R.string.data_modified, Toast.LENGTH_SHORT).show();
                                getTeamData();
//                                finish();
                            } else {
                                SharedUtils.handleServerError(BreakDownActivity.this, response);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        if (!updateTargetTask.isCanceled()) {
                            progress.setVisibility(View.GONE);
                            SharedUtils.networkErrorDialogWithRetry(BreakDownActivity.this, retry);
                        }
                    }
                });
            }else{
                progress.setVisibility(View.GONE);
                Log.d("TestTarget", "submitNewTarget: "+jsonArrays.toString());
            }

    }

    private void setBottomView(){
        BottomView bottomView = (BottomView) findViewById(R.id.bottom_view);
        final MenuView menuView = (MenuView) findViewById(R.id.menu_view);
        bottomView.setMenuView(menuView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SharedUtils.serverIsHongKong(this)) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.submit_btn:
//                Log.d("BreakDownActivity", "onClick: submit target");
//                submitNewTarget();
//                break;
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        adapter.scrollImage(scrollY);

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK && data != null){
            if(SharedUtils.serverIsHongKong(this)){
                getTeamPage(getStartDate(cal), getEndData(cal), null, null);
            }else{
                String id = data.getStringExtra("userId");
                updateUser(id, data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (getTeamPageTask != null){
            getTeamPageTask.cancel();
        }
        if (adapter != null){
            adapter.cancelImageTask();
        }
        super.onDestroy();
    }
}
