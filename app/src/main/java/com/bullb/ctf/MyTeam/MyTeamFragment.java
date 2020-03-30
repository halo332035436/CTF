package com.bullb.ctf.MyTeam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bullb.ctf.Model.Score;
import com.bullb.ctf.R;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

public class MyTeamFragment extends Fragment implements View.OnClickListener, ObservableScrollViewCallbacks {
    private static final String SCORES = "scores";
    private static final String DID = "did";
    private Score[] scores;
    private String department_id;

    public MyTeamRecyclerViewAdapter adapter;
    private ObservableRecyclerView recyclerView;

    public MyTeamFragment() {
        // Required empty public constructor
    }

    public static MyTeamFragment newInstance(Score[] s, String did) {
        MyTeamFragment fragment = new MyTeamFragment();
        Bundle args = new Bundle();
        args.putSerializable(SCORES, s);
        args.putString(DID, did);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            scores = (Score[]) getArguments().getSerializable(SCORES);
            department_id = getArguments().getString(DID);
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new MyTeamRecyclerViewAdapter(getActivity(), scores, department_id,mLayoutManager);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setScrollViewCallbacks(this);
    }

    public void setScore(Score score){
        int position = ((MyTeamActivity)getActivity()).edit_position;
        adapter.setScore(position, score);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        adapter.scrollImage(scrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onDetach() {
        if (adapter != null){
            adapter.cancelImageTask();
        }
        super.onDetach();
    }
}
