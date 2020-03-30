package com.bullb.ctf.TargetManagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.MyTargetPageResponse;
import com.bullb.ctf.API.Response.TeamTargetPageResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.DepartmentSalesData;
import com.bullb.ctf.Model.DepartmentTargetData;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.Sales;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CalendarView;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SummaryTargetManagementFragment extends Fragment implements ObservableScrollViewCallbacks, CalendarView.OnCalendarClickListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public final int PREDICT_COMMISSION_REQUEST = 101;
    private final String MODE_USERS = "users";
    private final String MODE_DEPARTMENTS = "departments";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TargetRecyclerViewAdapter_A adapterA;
    private TargetRecyclerViewAdapter_B adapterB;
    private TargetRecyclerViewAdapter_C adapterC;

    private TargetRecyclerViewAdapter_Summary adapterSummary;

    private ObservableRecyclerView recyclerView;
    private ArrayList<String> dataList;

    public static String rank;
    private String userType;

    private ApiService apiService;
    private Call<BaseResponse> getMyTargetPageTask;
    private Call<BaseResponse> getTeamPageTask;
    private KeyTools keyTools;
    public CalendarView calendarView;
    private AVLoadingIndicatorView progress;
    private CommonTabLayout tabLayout;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Department> departmentList = new ArrayList<>();
    private Department department;

    private ArrayList<Department> viewableList = new ArrayList<>();

    private User user;

    public SummaryTargetManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TargetManagementFragment.
     */
    public static SummaryTargetManagementFragment newInstance(String param1, String param2) {
        SummaryTargetManagementFragment fragment = new SummaryTargetManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_target_management, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (ObservableRecyclerView)view.findViewById(R.id.target_recycler_view);
        calendarView = (CalendarView)getActivity().findViewById(R.id.calendar_view);
        progress = (AVLoadingIndicatorView)getActivity().findViewById(R.id.progress);
        tabLayout = (CommonTabLayout)getActivity().findViewById(R.id.period_tablayout);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userType = mParam1;
        apiService = ServiceGenerator.createService(ApiService.class, getActivity());
        keyTools = KeyTools.getInstance(getActivity());
        user = SharedPreference.getUser(getActivity());

        department = new Gson().fromJson(getActivity().getIntent().getStringExtra("department"), Department.class);
        initUi();

        dateChange();
    }

    private void initUi(){
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0){
                    calendarView.setType(CalendarView.TYPE_MONTH);
                    dateChange();
                }
                else if (position == 1){
                    calendarView.setType(CalendarView.TYPE_QUARTER);
                    dateChange();
                }
                else if (position == 2){
                    calendarView.setType(CalendarView.TYPE_YEAR);
                    dateChange();
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        calendarView.setCalendarClickListener(this);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setScrollViewCallbacks(this);

        dataList = new ArrayList<>();
//        if (userType.equals(User.USER_TYPE_A)){
//            dataList.add("");
//            dataList.add("");
//            dataList.add("");
//            if (ServerPreference.getServerVersion(getActivity()).equals(ServerPreference.SERVER_VERSION_HK)) {
//                dataList.add("");
//            }
//
//            SalesData salesData = SharedPreference.getSales(getActivity());
//            UserTargetData userTargetData = SharedPreference.getTarget(getActivity());
//            adapterA = new TargetRecyclerViewAdapter_A(getActivity(), this, dataList, userType, userTargetData, salesData);
//            recyclerView.setAdapter(adapterA);
//        }
//        else if (userType.equals(User.USER_TYPE_B)){
//            String longDescription = "";
////            String shortDescription = "";
//            if (department != null){
//                longDescription = department.long_description;
////                shortDescription = department.short_description;
//            } else {
//                longDescription = user.getLongDepartmentName();
////                shortDescription = user.getShortDepartmentName();
//            }
//
//            adapterB = new TargetRecyclerViewAdapter_B(getActivity(), this, userList, userType, longDescription, longDescription);
//            recyclerView.setAdapter(adapterB);
//            //preset data from type c loaded data
//            if (department != null){
//                adapterB.setData(null, null, new DepartmentTargetData(department.department_targets, null, calendarView.getStartDate()), new DepartmentSalesData(department.departmentSales,calendarView.getStartDate()), calendarView.getStartDate());
//            }
//        }
//        else if (userType.equals(User.USER_TYPE_C)){
            String longDescription = getContext().getResources().getString(R.string.summary);

            viewableList.add(new Department());
            viewableList.addAll(user.getViewable_root_departments());


            adapterC = new TargetRecyclerViewAdapter_C(getActivity(), this, departmentList, userType);
            adapterSummary = new TargetRecyclerViewAdapter_Summary(getActivity(), this,viewableList,userType);
            recyclerView.setAdapter(adapterSummary);
//            recyclerView.setAdapter(adapterC);
            if (department != null){
//                adapterC.setData(null, null, new DepartmentTargetData(department.department_targets, null, calendarView.getStartDate()), new DepartmentSalesData(department.departmentSales,calendarView.getStartDate()), calendarView.getStartDate());
                adapterSummary.setData(null, null, new DepartmentTargetData(department.department_targets, null, calendarView.getStartDate()), new DepartmentSalesData(department.departmentSales,calendarView.getStartDate()), calendarView.getStartDate());

            }
//        }

    }


    private void dateChange(){
//        if (userType.equals(User.USER_TYPE_A)) {
////            getTypeAPageData();
//        } else if (userType.equals(User.USER_TYPE_B)){
//            String department_id;
//            if (department != null){
//                department_id = department.id;
//            } else{
//                department_id = null;
//            }
//            getTeamPage(calendarView.getStartDate(), calendarView.getEndDate(),null, department_id);
//        } else if(userType.equals(User.USER_TYPE_C)){
            String department_id;
            if (department != null){
                department_id = department.id;
            } else{
                department_id = null;
            }
            getTeamPage(calendarView.getStartDate(), calendarView.getEndDate(),MODE_DEPARTMENTS, department_id);
//        }
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
        final Call<BaseResponse> getTeamPageSubTask = apiService.getTeamTargetTask("Bearer " + SharedPreference.getToken(getActivity()), from_date, to_date, mode, null, "percentage");
        getTeamPageTask = getTeamPageSubTask;

        getTeamPageSubTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getTeamPageSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        TeamTargetPageResponse temp = new Gson().fromJson(data, TeamTargetPageResponse.class);
//                        if (userType.equals(User.USER_TYPE_B)) {
//                            setPageDataB(temp);
//                        }else if (userType.equals(User.USER_TYPE_C)){
                            setPageDataC(temp);
//                        }
                    } else {
                        SharedUtils.handleServerError(getActivity(), response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getTeamPageSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetry(getActivity(), retry);
                }
            }
        });
    }

    private void setPageDataA(MyTargetPageResponse response){
        Ranks rank = response.getRank();
        UserTargetData userTargetData = new UserTargetData(response.user_targets, 0, 0, calendarView.getStartDate());
        SalesData salesData = new SalesData(response.user_sales,new Sales(),new Sales(),getActivity(),calendarView.getStartDate());
        adapterA.setData(userTargetData, salesData, response.bonus_rates, rank);
    }

    private void setPageDataB(TeamTargetPageResponse response){
        userList.clear();
        if (response.users != null) {
            Collections.sort(response.users, new Comparator<User>() {
                @Override
                public int compare(User lhs, User rhs) {
                    SalesData lhsSales = new SalesData(lhs.user_sales, calendarView.getStartDate());
                    SalesData rhsSales = new SalesData(rhs.user_sales, calendarView.getStartDate());
                    return Double.compare(rhsSales.getSalesCompletionRate(),lhsSales.getSalesCompletionRate());
                }
            });
            userList.addAll(response.users);
        }
        DepartmentTargetData targetData = new DepartmentTargetData(response.department_targets,userList,calendarView.getStartDate());
        DepartmentSalesData salesData = new DepartmentSalesData(response.departmentSales,calendarView.getStartDate());
        adapterB.setData(response.ranks, response.parent, targetData, salesData, calendarView.getStartDate());
    }

    private void setPageDataC(TeamTargetPageResponse response){
        departmentList.clear();
        if (response.children != null) {
            Collections.sort(response.children, new Comparator<Department>() {
                @Override
                public int compare(Department lhs, Department rhs) {
                    DepartmentSalesData lhsSales = new DepartmentSalesData(lhs.departmentSales,calendarView.getStartDate());
                    DepartmentSalesData rhsSales = new DepartmentSalesData(rhs.departmentSales,calendarView.getStartDate());
                    return Double.compare(rhsSales.getSalesCompleteRate(),lhsSales.getSalesCompleteRate());
                }
            });
            departmentList.addAll(response.children);
        }
        DepartmentTargetData targetData = new DepartmentTargetData(response.department_targets, null,calendarView.getStartDate());
        DepartmentSalesData salesData = new DepartmentSalesData(response.departmentSales,calendarView.getStartDate());
        adapterC.setData(response.ranks, response.parent, targetData, salesData, calendarView.getStartDate());
        adapterSummary.setData(response.ranks, response.parent, targetData, salesData, calendarView.getStartDate());
    }

    @Override
    public void onBackwardClick() {
        dateChange();
    }

    @Override
    public void onForwardClick() {
        dateChange();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        if (mParam1.equals(User.USER_TYPE_A)){
//            adapterA.scrollImage(scrollY);
//        }else if (mParam1.equals(User.USER_TYPE_B)){
//            adapterB.scrollImage(scrollY);
//        }else if (mParam1.equals(User.USER_TYPE_C)){
//            adapterC.scrollImage(scrollY);
            adapterSummary.scrollImage(scrollY);
//        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREDICT_COMMISSION_REQUEST && resultCode == getActivity().RESULT_OK && data != null){
            UserTargetData userTargetData = adapterA.getUserTargetData();
            userTargetData.setCurrentMonthUserTarget(data.getDoubleExtra("adjust_a",0),data.getDoubleExtra("adjust_f",0),data.getDoubleExtra("adjust_e",0), data.getDoubleExtra("adjust_m",0), calendarView.getStartDate());
            userTargetData.calculateTarget(calendarView.getStartDate());
            adapterA.setData(userTargetData);
            SharedPreference.setTarget(userTargetData, getActivity());
        }
    }

    @Override
    public void onDetach() {
        if (getMyTargetPageTask != null){
            getMyTargetPageTask.cancel();
        }
        if (getTeamPageTask != null){
            getTeamPageTask.cancel();
        }
        super.onDetach();
    }


}
