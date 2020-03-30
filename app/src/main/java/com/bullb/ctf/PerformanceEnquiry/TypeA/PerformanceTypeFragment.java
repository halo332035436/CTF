package com.bullb.ctf.PerformanceEnquiry.TypeA;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.PerformanceEnquiry.PerformanceEnquiryActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.Widget.CalendarView;
import com.bullb.ctf.Widget.CircleView;
import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerformanceTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerformanceTypeFragment extends Fragment implements View.OnClickListener{
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
    private int angle;
    private String type;
    private View.OnClickListener listener;
    private CircleData data1, data2;
    private String rank, rankDes;
    private TextView checkDetailBtn;
    private int position;
    private CalendarView calendarView;


    public PerformanceTypeFragment() {
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
    public static PerformanceTypeFragment newInstance(String param1, String param2, int angle, String type, int position) {
        PerformanceTypeFragment fragment = new PerformanceTypeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, angle);
        args.putString(ARG_PARAM4, type);
        args.putInt(ARG_PARAM5, position);
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
            position = getArguments().getInt(ARG_PARAM5);
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
        checkDetailBtn = (TextView)view.findViewById(R.id.check_detail_btn);
        calendarView = (CalendarView)getActivity().findViewById(R.id.calendar_view);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUi();
        circleView.setOnClickListener(listener);
    }

    private void initUi(){
        checkDetailBtn.setOnClickListener(this);
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
            circleView.startCircleAnim(angle);
    }

    public void setIsFirst(boolean isFirst){
        this.isFirst = isFirst;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.check_detail_btn:
                Intent intent = new Intent();
                intent.setClass(getActivity(), PerformanceDetailActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("from_date", calendarView.getStartDate());
                intent.putExtra("to_date", calendarView.getEndDate());
                intent.putExtra("user", new Gson().toJson(((PerformanceEnquiryActivity)getActivity()).user));
                intent.putExtra("long_des", ((PerformanceEnquiryActivity)getActivity()).longDescription);
                startActivity(intent);
                break;
        }
    }
}
