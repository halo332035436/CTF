package com.bullb.ctf.PerformanceEnquiry;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CircleView;
import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CircleWithTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CircleWithTypeFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private TextView typeText,  rankText, rankTitle;
    private CircleView circleView;
    private boolean isFirst = false;

    private String mParam1;
    private String mParam2;
    private double angle;
    private String type;
    private View.OnClickListener listener;
    private CircleData data1, data2;
    private String rank, rankDes;
    public CircleWithTypeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CircleWithTypeFragment.
     */
    public static CircleWithTypeFragment newInstance(String param1, String param2, int angle, String type) {
        CircleWithTypeFragment fragment = new CircleWithTypeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, angle);
        args.putString(ARG_PARAM4, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            angle = getArguments().getInt(ARG_PARAM3);
            type = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_circle_with_type, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        typeText = (TextView)view.findViewById(R.id.type_text);
        circleView = (CircleView)view.findViewById(R.id.circle_view);
        rankText = (TextView)view.findViewById(R.id.rank);
        rankTitle = (TextView)view.findViewById(R.id.rank_title);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUi();
        circleView.setOnClickListener(listener);
    }

    private void initUi(){
        Gson gson = new Gson();

        if (data1 == null && mParam1 != null){
            data1 = gson.fromJson(mParam1,CircleData.class);
        }
        if (data2 == null &&  mParam2 != null){
            data2 = gson.fromJson(mParam2,CircleData.class);
        }

        circleView.setData(data1,data2);
        typeText.setText(type);
    }

    public void putData(CircleData data1, CircleData data2, double rate, String rank, String rankDes){
        this.data1 = data1;
        this.data2 = data2;
        this.rank = rank;
        this.rankDes = rankDes;
        this.angle = rate;
        if (circleView != null) {
            circleView.setData(data1, data2);
            rankText.setText(rank);
            rankTitle.setText(rankDes);
            startCircleViewAnimation();
        }
    }


    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
        if (circleView != null){
            circleView.setOnClickListener(listener);
        }
    }

    public int[] getCircleViewLocation(){
        int[] locations = new int[2];
        circleView.getLocationOnScreen(locations);
        return locations;
    }



    public void startCircleViewAnimation(){
        if (circleView != null)
            circleView.startCircleAnim((int)SharedUtils.formatDouble(angle));
    }

    public void setIsFirst(boolean isFirst){
        this.isFirst = isFirst;
    }


}
