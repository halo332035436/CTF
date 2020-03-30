package com.bullb.ctf.ShareFragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CircleAngleAnimation;
import com.bullb.ctf.Widget.CircleView;
import com.google.gson.Gson;


public class CircleFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String mParam1;
    private String mParam2;
    private int mParam3;

    private CircleView circleView;
    private View.OnClickListener listener = null;
    private boolean isFirst = false;
    private CircleData data1, data2;

    public CircleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TotalBonusFragment.
     */
    public static CircleFragment newInstance(String param1, String param2, int animAngle) {
        CircleFragment fragment = new CircleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, animAngle);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_total_bonus, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        circleView = (CircleView)view.findViewById(R.id.circle_view);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        circleView.setOnClickListener(listener);
    }

    private void init(){
        Gson gson = new Gson();


        if (data1 == null && mParam1 != null){
            data1 = gson.fromJson(mParam1,CircleData.class);
        }
        if (data2 == null &&  mParam2 != null){
            data2 = gson.fromJson(mParam2,CircleData.class);
        }

        circleView.setData(data1,data2);
//        if (isFirst) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    startCircleViewAnimation();
//                }
//            }, 200);
//        }
//        else{
//            circleView.setCircleAngle(mParam3);
//        }
    }

    public void putData(CircleData data1, CircleData data2){
        this.data1 = data1;
        this.data2 = data2;
        if (circleView != null) {
            circleView.setData(data1, data2);
        }
    }

    public void putData(CircleData data1, CircleData data2, double angle ){
        this.data1 = data1;
        this.data2 = data2;
        mParam3 = (int)SharedUtils.formatDouble(angle);

        if (circleView != null) {
            circleView.setData(data1, data2);
            startCircleViewAnimation();
        }
    }

    public void setCircleViewOnClickListener(View.OnClickListener listener){
        this.listener = listener;
        if (circleView != null)
            circleView.setOnClickListener(listener);
    }

    public void startCircleViewAnimation(){
        if (circleView != null)
            circleView.startCircleAnim(mParam3);
    }

    public void setIsFirst(boolean isFirst){
        this.isFirst = isFirst;
    }
}
