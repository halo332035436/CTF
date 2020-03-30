package com.bullb.ctf.SalesRanking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bullb.ctf.Model.LeaderboardData;
import com.bullb.ctf.R;

public class RankingFragment extends Fragment {
    public RankingRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    public RankingFragment() {
        // Required empty public constructor
    }

    public static RankingFragment newInstance(String param1, String param2) {
        RankingFragment fragment = new RankingFragment();
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
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)view.findViewById(R.id.rank_recyclerview);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new RankingRecyclerViewAdapter(getActivity(), mLayoutManager);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    public void setData(LeaderboardData[] leaderboardData, String sortMethod){
        adapter.setData(leaderboardData);
        adapter.setRankType(sortMethod);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (adapter != null){
            adapter.cancelTask();
        }
        super.onDestroy();
    }
}
