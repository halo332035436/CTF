package com.bullb.ctf.SalesRanking.RankingDetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bullb.ctf.API.Response.MyTargetPageResponse;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.R;
import com.bullb.ctf.Widget.CalendarView;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

public class RankingDetailFragment extends Fragment implements ObservableScrollViewCallbacks {
    private RankingDetailRecyclerViewAdapter adapter;
    private ObservableRecyclerView recyclerView;

    public RankingDetailFragment() {
        // Required empty public constructor
    }

    public static RankingDetailFragment newInstance() {
        RankingDetailFragment fragment = new RankingDetailFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            userSales = (UserSale) getArguments().getSerializable(SALES);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_target_management, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (ObservableRecyclerView)view.findViewById(R.id.target_recycler_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new RankingDetailRecyclerViewAdapter(getActivity(), this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setScrollViewCallbacks(this);
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

    public void setData(MyTargetPageResponse myTarget){
        adapter.setData(myTarget, ((CalendarView)(getActivity().findViewById(R.id.calendar_view))).getStartDate());
        adapter.notifyDataSetChanged();
    }
}
