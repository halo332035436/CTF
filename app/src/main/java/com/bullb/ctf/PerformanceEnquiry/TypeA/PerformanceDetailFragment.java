package com.bullb.ctf.PerformanceEnquiry.TypeA;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Sales;
import com.bullb.ctf.Model.SalesDetail;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerformanceDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerformanceDetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private int position, currentPage;
    private String mParam2, fromDate, toDate;
    private RecyclerView recyclerView;
    private ArrayList<SalesDetail> dataList;
    private PerformanceDetailRecyclerViewAdapter adapter;
    private ApiService apiService;
    private KeyTools keyTools;
    private Call<BaseResponse> getSalesDetailTask;
    private boolean isSet = false;
    private ProgressBar progress;
    private TextView totalText, noDataText;
    private String pageType;
    private Calendar cal;

    public PerformanceDetailFragment() {
        // Required empty public constructor
    }


    public static PerformanceDetailFragment newInstance(int param1, String fromDate, String toDate, int currentPage, String pageType) {
        PerformanceDetailFragment fragment = new PerformanceDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, fromDate);
        args.putString(ARG_PARAM3, toDate);
        args.putInt(ARG_PARAM4, currentPage);
        args.putString(ARG_PARAM5, pageType);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_PARAM1);
            fromDate = getArguments().getString(ARG_PARAM2);
            toDate = getArguments().getString(ARG_PARAM3);
            currentPage = getArguments().getInt(ARG_PARAM4);
            pageType = getArguments().getString(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_performance_detail, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)view.findViewById(R.id.performance_detail_recyclerview);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        totalText = (TextView)view.findViewById(R.id.point_text);
        noDataText = (TextView)view.findViewById(R.id.no_data_text);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        apiService = ServiceGenerator.createService(ApiService.class, getActivity());
        keyTools = KeyTools.getInstance(getActivity());
        cal = Calendar.getInstance();

        dataList = new ArrayList<>();


        adapter = new PerformanceDetailRecyclerViewAdapter(getActivity(), dataList);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        if (currentPage == position) {
            setSalesDetails();
        }


    }

    public void setSalesDetails(){
        if (!isSet && recyclerView != null){
            getSalesDetails();
        }
    }

    private void getSalesDetails(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSalesDetails();
            }
        };

        String type;
        if (position ==1){
            type = Sales.TYPE_A;
        } else if (position ==2){
            type = Sales.TYPE_F;
        } else if (position ==3){
            type = Sales.TYPE_E;
        } else if (position ==4){
            type = Sales.TYPE_M;
        }
        else{
            type = "";
        }

        if (pageType != null && pageType.equals("refund")){
            getSalesDetailTask  = apiService.getSalesDetailTask("Bearer " + SharedPreference.getToken(getActivity()), SharedUtils.getFirstDay(cal) ,  SharedUtils.getLastDay(cal), type, ((PerformanceDetailActivity)getActivity()).user.id, null,null, "0");
        }
        else if (pageType != null && pageType.equals("sales")){
            getSalesDetailTask  = apiService.getSalesDetailTask("Bearer " + SharedPreference.getToken(getActivity()), SharedUtils.getFirstDay(cal) ,  SharedUtils.getLastDay(cal), type, ((PerformanceDetailActivity)getActivity()).user.id, null,"0", null);
        }
        else{
            getSalesDetailTask  = apiService.getSalesDetailTask("Bearer " + SharedPreference.getToken(getActivity()), fromDate, toDate, type, ((PerformanceDetailActivity)getActivity()).user.id, null,null, null);
        }


        progress.setVisibility(View.VISIBLE);
        getSalesDetailTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getSalesDetailTask.isCanceled()){
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        ArrayList<SalesDetail> tempList = new Gson().fromJson(data, new TypeToken<ArrayList<SalesDetail>>(){}.getType());
                        dataList.addAll(tempList);
                        adapter.notifyDataSetChanged();
                        isSet = true;
                        if (dataList == null || dataList.size() == 0){
                            noDataText.setVisibility(View.VISIBLE);
                        }
                        totalText.setText(getTotal(dataList));
                    } else {
                        SharedUtils.handleServerError(getActivity(), response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getSalesDetailTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(getActivity(), retry);
                }
            }
        });
    }

    private String getTotal(ArrayList<SalesDetail> salesDetails){
        double total = 0;
        if (salesDetails == null || salesDetails.size() ==0) {
           return "";
        }
        else{
            for (SalesDetail salesDetail: salesDetails){
                total += salesDetail.amount;
            }
        }
        return SharedUtils.addCommaToNum(total);

    }


    @Override
    public void onDestroy() {
        if (getSalesDetailTask != null){
            getSalesDetailTask.cancel();
        }
        super.onDestroy();
    }

}
