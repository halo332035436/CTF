package com.bullb.ctf.PerformanceEnquiry.TypeB;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.TeamTargetPageResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.DepartmentSalesData;
import com.bullb.ctf.Model.DepartmentTargetData;
import com.bullb.ctf.Model.Parent;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
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


public class PerformanceEnquiryBFragment extends Fragment implements ObservableScrollViewCallbacks, CalendarView.OnCalendarClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "cal_type";

    private String mParam1;
    private int mParam2;

    private PerformanceBRecyclerViewAdapter adapter;
    private ObservableRecyclerView recyclerView;
    private ArrayList<User> dataList;
    private ApiService apiService;
    private Call<BaseResponse> getTeamPageTask;
    private KeyTools keyTools;
    public CalendarView calendarView;
    private AVLoadingIndicatorView progress;
    private User user;
    private CommonTabLayout tabLayout;
    private Department department;

    public PerformanceEnquiryBFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerformanceEnquiryBFragment.
     */
    public static PerformanceEnquiryBFragment newInstance(String param1, int param2) {
        PerformanceEnquiryBFragment fragment = new PerformanceEnquiryBFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_performance_enquiry_b, container, false);
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
        apiService = ServiceGenerator.createService(ApiService.class, getActivity());
        keyTools = KeyTools.getInstance(getActivity());
        user = SharedPreference.getUser(getActivity());

        initUi();

        dateChange();
    }

    private void initUi(){
        calendarView.setCalendarClickListener(this);

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

        String longDescription = "";
//        String shortDescription = "";
        if (department != null){
            longDescription = department.long_description;
//            shortDescription = department.short_description;
        } else {
            longDescription = user.getLongDepartmentName();
//            shortDescription = user.getShortDepartmentName();
        }

        dataList = new ArrayList<>();
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new PerformanceBRecyclerViewAdapter(getActivity(), dataList, this, longDescription, longDescription);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setScrollViewCallbacks(this);

        //presetData
        if (department != null)
            adapter.setData(new Ranks(), new Parent(), new DepartmentTargetData(department.department_targets, null, calendarView.getStartDate()), new DepartmentSalesData(department.departmentSales,calendarView.getStartDate()), calendarView.getStartDate());

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
        final Call<BaseResponse> getTeamPageSubTask = apiService.getTeamTargetTask("Bearer " + SharedPreference.getToken(getActivity()), from_date, to_date, mode, department_id,"sale");
        getTeamPageTask = getTeamPageSubTask;

        getTeamPageSubTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getTeamPageSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        TeamTargetPageResponse temp = new Gson().fromJson(data, TeamTargetPageResponse.class);
                        setPageData(temp);
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

    public void presetData(Department department){
        this.department = department;
    }



    private void setPageData(TeamTargetPageResponse response){
        dataList.clear();
        if (response.users != null) {
            Collections.sort(response.users, new Comparator<User>() {
                @Override
                public int compare(User lhs, User rhs) {
                    SalesData lhsSales = new SalesData(lhs.user_sales, calendarView.getStartDate());
                    SalesData rhsSales = new SalesData(rhs.user_sales, calendarView.getStartDate());
                    return Double.compare(rhsSales.getTotalSales(),lhsSales.getTotalSales());
                }
            });
            dataList.addAll(response.users);
        }
        DepartmentTargetData targetData = new DepartmentTargetData(response.department_targets,null,calendarView.getStartDate());
        DepartmentSalesData salesData = new DepartmentSalesData(response.departmentSales,calendarView.getStartDate());
        adapter.setData(response.ranks, response.parent, targetData, salesData, calendarView.getStartDate());
    }

    private void dateChange() {
        String department_id =null;
        if (department != null){
            department_id = department.id;
        }
        getTeamPage(calendarView.getStartDate(), calendarView.getEndDate(),null, department_id);
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
        adapter.scrollImage(scrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onDetach() {
        if (getTeamPageTask != null){
            getTeamPageTask.cancel();
        }

        super.onDetach();
    }


}
