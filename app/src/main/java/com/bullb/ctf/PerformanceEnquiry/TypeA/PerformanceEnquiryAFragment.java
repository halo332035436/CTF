package com.bullb.ctf.PerformanceEnquiry.TypeA;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bullb.ctf.Widget.BlurCircleView;
import com.bullb.ctf.Widget.CalendarView;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerformanceEnquiryAFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerformanceEnquiryAFragment extends Fragment implements View.OnClickListener, CalendarView.OnCalendarClickListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView rankTitleText, rankText;
    private ViewPager pager;
    private CircleIndicator circleIndicator;

    private ApiService apiService;
    private KeyTools keyTools;
    private Call<BaseResponse> getSalesTask;
    public CalendarView calendarView;
    private CommonTabLayout tabLayout;
    private AVLoadingIndicatorView progress;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private User user;
    private UserTargetData targetData;
    private SalesData salesData;
    private String shortDescription;
    private String longDescription;

    public PerformanceEnquiryAFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerformanceEnquiryAFragment.
     */
    public static PerformanceEnquiryAFragment newInstance(String param1, String param2) {
        PerformanceEnquiryAFragment fragment = new PerformanceEnquiryAFragment();
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
        return inflater.inflate(R.layout.fragment_performance_enquiry, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rankTitleText = (TextView)view.findViewById(R.id.rank_title);
        rankText = (TextView)view.findViewById(R.id.rank);
        pager = (ViewPager)view.findViewById(R.id.view_pager);
        circleIndicator = (CircleIndicator)view.findViewById(R.id.indicator);
        calendarView = (CalendarView)getActivity().findViewById(R.id.calendar_view);
        tabLayout = (CommonTabLayout)getActivity().findViewById(R.id.period_tablayout);
        progress = (AVLoadingIndicatorView)getActivity().findViewById(R.id.progress);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiService = ServiceGenerator.createService(ApiService.class, getActivity());
        keyTools = KeyTools.getInstance(getActivity());

        if (user == null)
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


        Gson gson = new Gson();

        CircleData data = new CircleData(getString(R.string.total_sales_amount), "",CircleData.TYPE_MONEY);
        final PerformanceTypeFragment circleWithTypeFragment1 = PerformanceTypeFragment.newInstance(gson.toJson(data),null,0,getString(R.string.target_type_all),0);
        final PerformanceTypeFragment circleWithTypeFragment2 = PerformanceTypeFragment.newInstance(gson.toJson(data),null,0,getString(R.string.target_type_A),1);
        final PerformanceTypeFragment circleWithTypeFragment3 = PerformanceTypeFragment.newInstance(gson.toJson(data),null,0,getString(R.string.target_type_F),2);
        final PerformanceTypeFragment circleWithTypeFragment4 = PerformanceTypeFragment.newInstance(gson.toJson(data),null,0,getString(R.string.target_type_E),3);

        fragments.add(circleWithTypeFragment1);
        fragments.add(circleWithTypeFragment2);
        fragments.add(circleWithTypeFragment3);
        fragments.add(circleWithTypeFragment4);

        if (ServerPreference.getServerVersion(getActivity()).equals(ServerPreference.SERVER_VERSION_HK)) {
            final PerformanceTypeFragment circleWithTypeFragmentM = PerformanceTypeFragment.newInstance(gson.toJson(data),null,0,getString(R.string.target_type_M),4);
            fragments.add(circleWithTypeFragmentM);
        }

        PagerAdapter adapter = new PagerAdapter(getFragmentManager(), fragments);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(fragments.size());
        circleIndicator.setViewPager(pager);

        //preset Data
        if(SharedPreference.getUser(getActivity()).type.equals(User.USER_TYPE_A)) {
            targetData = SharedPreference.getTarget(getActivity());
            salesData = SharedPreference.getSales(getActivity());
        }
        if (targetData != null && salesData != null){
            setData(targetData, salesData, new Ranks());
        }
    }

    private void dateChange(){
        getSales();
    }

    public void presetData(UserTargetData targetData, SalesData salesData, User user, String longDescription, String shortDescription){
        this.targetData = targetData;
        this.salesData = salesData;
        this.user = user;
        this.longDescription = longDescription;
        this.shortDescription = shortDescription;
    }

    private void getSales(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSales();
            }
        };

        if (getSalesTask != null)
            getSalesTask.cancel();

        progress.setVisibility(View.VISIBLE);
        final Call<BaseResponse> getSalesSubTask = apiService.getMyTargetPageTask("Bearer " + SharedPreference.getToken(getActivity()), calendarView.getStartDate(), calendarView.getEndDate() ,user.id, "sale");
        getSalesTask = getSalesSubTask;

        getSalesSubTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getSalesSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        MyTargetPageResponse temp = new Gson().fromJson(data, MyTargetPageResponse.class);
                        UserTargetData targetData = new UserTargetData(temp.user_targets,  calendarView.getStartDate());
                        SalesData salesData = new SalesData(temp.user_sales,calendarView.getStartDate());
                        Ranks ranks = temp.getRank();
                        setData(targetData, salesData, ranks);
                    } else {
                        SharedUtils.handleServerError(getActivity(), response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getSalesSubTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetry(getActivity(), retry);
                }
            }
        });
    }

    public void setData(final UserTargetData targetData, final SalesData salesData, final Ranks ranks){
        for (int i =0; i<fragments.size();i++ ){
            final PerformanceTypeFragment fragment = (PerformanceTypeFragment)fragments.get(i);
            if (i==0){
                CircleData data = new CircleData(getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getTotalSales()),CircleData.TYPE_MONEY);
                CircleData data2 = new CircleData(getString(R.string.last_year_sales), SharedUtils.addCommaToNum(salesData.getLastTotalSales()),CircleData.TYPE_MONEY);

                fragment.putData(data, data2,0,ranks.getAll(), shortDescription+ getString(R.string.rank));
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.circle_distributed_target), SharedUtils.addCommaToNum(targetData.getDistributedAllRate(salesData),"%"),CircleData.TYPE_TEXT);
                        CircleData data2 = new CircleData(getString(R.string.circle_adjusted_complete_rate),SharedUtils.addCommaToNum(targetData.getAdjustedAllRate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setData(data1,data2);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.show();
                    }
                });
            }else if (i ==1){
                CircleData data = new CircleData(getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getSalesA()),CircleData.TYPE_MONEY);
                CircleData data2 = new CircleData(getString(R.string.last_year_sales), SharedUtils.addCommaToNum(salesData.getLastA()),CircleData.TYPE_MONEY);

                fragment.putData(data, data2,0,ranks.getA(), shortDescription+ getString(R.string.rank));
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.circle_distributed_target), SharedUtils.addCommaToNum(targetData.getDistributedARate(salesData),"%"),CircleData.TYPE_TEXT);
                        CircleData data2 = new CircleData(getString(R.string.circle_adjusted_complete_rate),SharedUtils.addCommaToNum(targetData.getAdjustedARate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setData(data1,data2);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.show();
                    }
                });
            }else if (i==2){
                CircleData data = new CircleData(getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getSalesF()),CircleData.TYPE_MONEY);
                CircleData data2 = new CircleData(getString(R.string.last_year_sales), SharedUtils.addCommaToNum(salesData.getLastF()),CircleData.TYPE_MONEY);

                fragment.putData(data, data2,0,ranks.getF(),  shortDescription+ getString(R.string.rank));
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.circle_distributed_target), SharedUtils.addCommaToNum(targetData.getDistributedFRate(salesData),"%"),CircleData.TYPE_TEXT);
                        CircleData data2 = new CircleData(getString(R.string.circle_adjusted_complete_rate),SharedUtils.addCommaToNum(targetData.getAdjustedFRate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setData(data1,data2);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.show();
                    }
                });
            }else if (i==3){
                CircleData data = new CircleData(getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getSalesE()),CircleData.TYPE_MONEY);
                CircleData data2 = new CircleData(getString(R.string.last_year_sales), SharedUtils.addCommaToNum(salesData.getLastE()),CircleData.TYPE_MONEY);

                fragment.putData(data, data2,0,ranks.getE(),  shortDescription + getString(R.string.rank));
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.circle_distributed_target), SharedUtils.addCommaToNum(targetData.getDistributedERate(salesData),"%"),CircleData.TYPE_TEXT);
                        CircleData data2 = new CircleData(getString(R.string.circle_adjusted_complete_rate),SharedUtils.addCommaToNum(targetData.getAdjustedERate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setData(data1,data2);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.show();
                    }
                });
            }else if (i==4){
                CircleData data = new CircleData(getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getSalesM()),CircleData.TYPE_MONEY);
                CircleData data2 = new CircleData(getString(R.string.last_year_sales), SharedUtils.addCommaToNum(salesData.getLastM()),CircleData.TYPE_MONEY);

                fragment.putData(data, data2,0,ranks.getM(),  shortDescription + getString(R.string.rank));
                fragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                        CircleData data1 = new CircleData(getString(R.string.circle_distributed_target), SharedUtils.addCommaToNum(targetData.getDistributedMRate(salesData),"%"),CircleData.TYPE_TEXT);
                        CircleData data2 = new CircleData(getString(R.string.circle_adjusted_complete_rate),SharedUtils.addCommaToNum(targetData.getAdjustedMRate(salesData),"%"),CircleData.TYPE_TEXT);
                        blurView.setData(data1,data2);
                        blurView.setLocation(fragment.getCircleViewLocation());
                        blurView.show();
                    }
                });
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }
    }

    @Override
    public void onDestroy() {
        if (getSalesTask != null){
            getSalesTask.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onBackwardClick() {
        dateChange();
    }

    @Override
    public void onForwardClick() {
        dateChange();
    }
}
