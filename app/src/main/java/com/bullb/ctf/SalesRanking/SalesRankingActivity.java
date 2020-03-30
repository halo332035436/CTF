package com.bullb.ctf.SalesRanking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.Descendants;
import com.bullb.ctf.Model.KeyValue;
import com.bullb.ctf.Model.LeaderboardData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserRole;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.Date;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.FilterView;
import com.bullb.ctf.Widget.MenuView;
import com.bullb.ctf.Widget.TabEntity;
import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesRankingActivity extends AppCompatActivity implements View.OnClickListener {
    private final int CAT_ALL = 0, CAT_A = 1, CAT_F = 2, CAT_E = 3, CAT_M = 4;
    public static final int DATA_STAFF = 0;
    public final static String SORT_BY_SALE = "sale";
    public final static String SORT_BY_PERCENTAGE = "percentage";
    private ImageView layerBtn;

    private static final int SELECT_LAYER_CODE = 1212;

    private ApiService apiService;
    private KeyTools keyTools;

    private User user;
    private Date date;
    private Call<BaseResponse> getLeaderboardTask;
    private Call<BaseResponse> getDepartmentDescendantsTask;

    private FragmentManager fm;
    private RankingFragment fragment;
    private FilterView filterView;
    private AVLoadingIndicatorView progress;
    private TextView locationText, rankTypeText;
    private ImageView filterBtn, rankingTypeBtn;
    private CommonTabLayout tabLayout;

    public Descendants[] descendants;

    private int selectedType = Date.TYPE_MONTH;
    private int category, selectedRank, selectedUnit = -1;
    private Descendants selectedDepartment;

    private static boolean sortedBySale = true;

    private static Department mDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_ranking);

        user = SharedPreference.getUser(this);
        if (user == null) return;

        setToolbar();
        setBottomView();
        initUi();

        date = new Date();
        apiService = ServiceGenerator.createService(ApiService.class,this);
        keyTools = KeyTools.getInstance(this);
        setUIData();
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
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);
        locationText = (TextView)findViewById(R.id.location_text);
        rankTypeText = (TextView)findViewById(R.id.rank_type);
        filterBtn = (ImageView)findViewById(R.id.filter_btn);
        tabLayout = (CommonTabLayout)findViewById(R.id.period_tablayout);
        filterView = (FilterView)findViewById(R.id.filter_view);
        rankingTypeBtn = (ImageView)findViewById(R.id.ranking_type_btn);
        layerBtn = (ImageView)findViewById(R.id.layer_btn);

//        if(user.getViewable_root_departments().size()>1){
//            layerBtn.setVisibility(View.VISIBLE);
//            layerBtn.setOnClickListener(this);
//        }else{
//            layerBtn.setVisibility(View.GONE);
//        }
        layerBtn.setVisibility(View.VISIBLE);
        layerBtn.setOnClickListener(this);
    }

    private void setUIData() {
        // The Filter button callback

        filterBtn.setOnClickListener(this);

        UserRole userRole = user.getUserRole();
        ArrayList<KeyValue> keyValues = new ArrayList<>();
        if (userRole != null)
            keyValues = userRole.getDetail();

        if (keyValues != null) {
            for (KeyValue keyValue : keyValues) {
                if(keyValue.getKey().equals("range_filter")){
                    if(keyValue.getValue()){
                        filterBtn.setOnClickListener(this);
                    }else{
                        filterBtn.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }

        rankingTypeBtn.setOnClickListener(this);
        filterView.addFilterDoneListener(new FilterView.FilterDoneListener() {
            @Override
            public void filterDoneCallBack(int cat, int sRank, int sUnit) {
                category = cat;
                selectedRank = sRank;
                selectedUnit = sUnit;

                if (selectedDepartment != null)
                locationText.setText(selectedDepartment.long_description);

                // Change TabLayout Title to selected category
                setRankTypeText();

                if (Objects.equals(user.type, User.USER_TYPE_A) || Objects.equals(user.type, User.USER_TYPE_B))
                    getLeaderboard(null);
                if (Objects.equals(user.type, User.USER_TYPE_C)){
                    getLeaderboard(selectedDepartment.id);
                }

            }
        });

        // Set Location info

        mDepartment = new Gson().fromJson(getIntent().getStringExtra("department"),Department.class);
        if(mDepartment==null){
            mDepartment = user.getDepartment();
        }

//        Descendants descendants = new Gson().fromJson(getIntent().getStringExtra("department"),Descendants.class);

//        if(mDepartment.getProperties()==0){
//            locationText.setText(user.unit);
//        }else {
//            locationText.setText(mDepartment.long_description);
//        }
        locationText.setText(user.getLongDepartmentName());

        // Set TabLayout
        setRankTypeText();
        setTabLayout();

        // Set Fragment
        fm = getSupportFragmentManager();
        fragment = RankingFragment.newInstance("", "");
        fm.beginTransaction().replace(R.id.frame, fragment).commit();

        if (Objects.equals(user.type, User.USER_TYPE_A) || Objects.equals(user.type, User.USER_TYPE_B))
            getLeaderboard(null);
        if (Objects.equals(user.type, User.USER_TYPE_C)) {

            Descendants descendants = new Descendants();
            descendants.id = user.getDepartment().id;
            selectedDepartment = descendants;

//            getLeaderboard(user.getDepartment().id);
            getLeaderboard(mDepartment.id);
        }
    }

    public void cancelGetDepartmentDescendantsTask(){
        if (getDepartmentDescendantsTask != null)
            getDepartmentDescendantsTask.cancel();
    }

    public void getDepartmentDescendants(final int pos, final String department_id, final boolean shouldUpdateLeaderboard) {
        filterView.showProgressBar();

        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDepartmentDescendants(pos ,department_id, shouldUpdateLeaderboard);
            }
        };

        String rankRange = "4";
        if (selectedRank == 1) rankRange = "3";
        if (selectedRank == 2) rankRange = "2";
        if (selectedRank == 3) rankRange = "1";

        if (getDepartmentDescendantsTask != null)
            getDepartmentDescendantsTask.cancel();

        final Call<BaseResponse> thisDepartmentDescendantsTask = apiService.getViewableDepartment(
                "Bearer " + SharedPreference.getToken(SalesRankingActivity.this), rankRange, "1");


        getDepartmentDescendantsTask = thisDepartmentDescendantsTask;

        thisDepartmentDescendantsTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                filterView.hideProgressBar();
                if (!thisDepartmentDescendantsTask.isCanceled()) {
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        descendants = new Gson().fromJson(data, Descendants[].class);
                        if (descendants == null){
                            descendants = new Descendants[0];
                        }
                        filterView.setDescendantsList(pos, descendants);
                    } else {
                        SharedUtils.handleServerError(SalesRankingActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!thisDepartmentDescendantsTask.isCanceled()){
                    filterView.hideProgressBar();
                    SharedUtils.networkErrorDialogWithRetryUncancellable(SalesRankingActivity.this, retry);
                }
            }
        });
    }

    private void setTabLayout(){
        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        tabEntities.add(new TabEntity(getString(R.string.monthly)));
        tabEntities.add(new TabEntity(getString(R.string.quarterly)));
        tabEntities.add(new TabEntity(getString(R.string.yearly)));

        tabLayout.setTabData(tabEntities);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0)
                    selectedType = Date.TYPE_MONTH;
                else if (position == 1)
                    selectedType = Date.TYPE_QUARTER;
                else if (position == 2)
                    selectedType = Date.TYPE_YEAR;

                if (Objects.equals(user.type, User.USER_TYPE_A) || Objects.equals(user.type, User.USER_TYPE_B))
                    getLeaderboard(null);
                if (Objects.equals(user.type, User.USER_TYPE_C))
                    getLeaderboard(mDepartment.id);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void setRankTypeText(){
        switch (category){
            case CAT_ALL:
                rankTypeText.setText(getString(R.string.target_type_all) + getString(R.string.rank));
                break;
            case CAT_A:
                rankTypeText.setText(getString(R.string.a_type) + getString(R.string.rank));
                break;
            case CAT_F:
                rankTypeText.setText(getString(R.string.f_type) + getString(R.string.rank));
                break;
            case CAT_E:
                rankTypeText.setText(getString(R.string.e_type) + getString(R.string.rank));
                break;
            case CAT_M:
                rankTypeText.setText(getString(R.string.m_type) + getString(R.string.rank));
                break;
        }
    }

    private void getLeaderboard(String department_id){
        if (Objects.equals(user.type, User.USER_TYPE_A) || Objects.equals(user.type, User.USER_TYPE_B))
            getLeaderboardData(selectedType, null);
        if (Objects.equals(user.type, User.USER_TYPE_C)){
            if (selectedUnit == -1 || selectedUnit ==0)
                getLeaderboardData(selectedType, department_id);
            else
                getLeaderboardDataForTypeC(selectedType);
        }
    }

    /**
     * Leaderboard API service
     * @param type e.g.TYPE_MONTH, TYPE_QUARTER, TYPE_YEAR
     */
    private void getLeaderboardData(final int type, final String department_id){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getLeaderboardData(type, department_id);
            }
        };

        progress.setVisibility(View.VISIBLE);

        String did = user.getDepartment().id;
        if (department_id != null) did = department_id;

        String from_date = date.getFromDate(type);
        String to_date = date.getToDate(type);
        String cat = null;
        if (category == 1) cat = "A";
        if (category == 2) cat = "F";
        if (category == 3) cat = "E";
        if (category == 4) cat = "M";

        final Call<BaseResponse> thisLeaderboardTask;
        if (selectedUnit == 0) {
            thisLeaderboardTask = apiService.getLeaderboardTask(
                    "Bearer " + SharedPreference.getToken(this), did,from_date, to_date, getSortMethod(), cat);
        }
        else{
            if (user.type.equals(user.USER_TYPE_A) || user.type.equals(user.USER_TYPE_B)){
                thisLeaderboardTask = apiService.getLeaderboardTask(
                        "Bearer " + SharedPreference.getToken(this), did,from_date, to_date, getSortMethod(), cat);
            }
            else{
                String prop;
                if (user.getDepartment().getProperties() >= 5 && user.getDepartment().getProperties() <=9){
                    prop = "1";
                }
                else {
                    prop = String.valueOf(user.getDepartment().getProperties() + 1);
                }
                thisLeaderboardTask = apiService.getLeaderboardTask(
                "Bearer " + SharedPreference.getToken(this), did,prop,from_date, to_date, getSortMethod(), cat);
//                "Bearer " + SharedPreference.getToken(this), did,prop,"2017-10-01", "2017-10-31", getSortMethod(), cat);
            }

        }

        if (getLeaderboardTask != null) getLeaderboardTask.cancel();
        getLeaderboardTask = thisLeaderboardTask;
        thisLeaderboardTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!thisLeaderboardTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        LeaderboardData[] leaderboardData = new Gson().fromJson(data, LeaderboardData[].class);
                        setLeaderboardData(leaderboardData);
                    } else {
                        SharedUtils.handleServerError(SalesRankingActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!thisLeaderboardTask.isCanceled()){
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(SalesRankingActivity.this, retry);
                }
            }
        });
    }

    /**
     * Leaderboard API service For Type C only
     * @param type e.g.TYPE_MONTH, TYPE_QUARTER, TYPE_YEAR
     */
    private void getLeaderboardDataForTypeC(final int type){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getLeaderboardDataForTypeC(type);
            }
        };

        progress.setVisibility(View.VISIBLE);
        String from_date = date.getFromDate(type);
        String to_date = date.getToDate(type);

        int sr = selectedUnit;
        String prop = "1";
        if (sr == 1) prop = "4";
        if (sr == 2) prop = "3";
        if (sr == 3) prop = "2";
        String cat = null;
        if (category == 1) cat = "A";
        if (category == 2) cat = "F";
        if (category == 3) cat = "E";
        if (category == 4) cat = "M";

        final Call<BaseResponse> thisLeaderboardTask = apiService.getLeaderboardTask(
                "Bearer " + SharedPreference.getToken(this), selectedDepartment.id, prop, from_date, to_date, getSortMethod(), cat);

        if (getLeaderboardTask != null) getLeaderboardTask.cancel();
        getLeaderboardTask = thisLeaderboardTask;
        thisLeaderboardTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!thisLeaderboardTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        LeaderboardData[] leaderboardData = new Gson().fromJson(data, LeaderboardData[].class);
                        setLeaderboardData(leaderboardData);
                    } else {
                        SharedUtils.handleServerError(SalesRankingActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!thisLeaderboardTask.isCanceled()){
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(SalesRankingActivity.this, retry);
                }
            }
        });
    }

    /**
     * Set Leaderboard List in fragment
     * @param leaderboardData
     */
    private void setLeaderboardData(LeaderboardData[] leaderboardData) {
        TextView e = (TextView) findViewById(R.id.is_empty);
        FrameLayout f = (FrameLayout) findViewById(R.id.frame);
        if (leaderboardData.length == 0){
            f.setVisibility(View.GONE);
            e.setVisibility(View.VISIBLE);
            return;
        }
        f.setVisibility(View.VISIBLE);
        e.setVisibility(View.GONE);
        fragment.setData(leaderboardData, getSortMethod());

    }

    public String getSortMethod(){
        if(sortedBySale){
            return SORT_BY_SALE;
        }else{
            return SORT_BY_PERCENTAGE;
        }
    }

    /**
     * Click on filter button
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.filter_btn:
                filterView.resetFilter();
                filterView.show(filterBtn);
                filterView.requestLayout();
                break;
            case R.id.ranking_type_btn:
                sortedBySale = !sortedBySale;
                if(sortedBySale){
                    Glide.with(SalesRankingActivity.this).load(R.drawable.valuesort_btn).into(rankingTypeBtn);
                } else{
                    Glide.with(SalesRankingActivity.this).load(R.drawable.percentsort_btn).into(rankingTypeBtn);
                }
                if (Objects.equals(user.type, User.USER_TYPE_A) || Objects.equals(user.type, User.USER_TYPE_B))
                    getLeaderboard(null);
                if (Objects.equals(user.type, User.USER_TYPE_C)) {
//                    getLeaderboard(user.getDepartment().id);
                    getLeaderboard(mDepartment.id);
                }
                break;
            case R.id.layer_btn:
                Intent layerIntent = new Intent();
                layerIntent.setClass(this, SalesRankingSelectActivity.class);
                startActivityForResult(layerIntent,SELECT_LAYER_CODE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (getLeaderboardTask != null) getLeaderboardTask.cancel();
        if (getDepartmentDescendantsTask != null) getDepartmentDescendantsTask.cancel();
        super.onDestroy();
    }

    public void setSelectedRank(int rank){
        selectedRank = rank;
    }

    public void setSelectedDepartmentInFilterView(Descendants department){
        selectedDepartment = department;
        mDepartment.id = department.id;
        mDepartment.long_description = department.long_description;
    }

    public Integer getSelectedType(){
        return selectedType;
    }

    public int getSelectedCategory(){
        return category;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_LAYER_CODE && resultCode==RESULT_OK){
            finish();
        }
    }
}
