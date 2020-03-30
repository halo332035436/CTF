package com.bullb.ctf.SelfManagement;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CircleView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private TextView completeRateText,targetCompleteText;
    private CircleView circleView;
    private int angle;
    private LinearLayout adjustedLayout;
    private User user;
    private UserTargetData data;
    private SalesData salesData;
    private TextView rateText;

    public DashBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashBoardFragment.
     */
    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
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
            angle = getArguments().getInt(ARG_PARAM3);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dash_board, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        circleView = (CircleView) view.findViewById(R.id.circle_view);
        targetCompleteText = (TextView)view.findViewById(R.id.target_complete_textview);
        completeRateText= (TextView)view.findViewById(R.id.complete_rate_textview);
        adjustedLayout = (LinearLayout)view.findViewById(R.id.adjusted_layout);
        rateText = (TextView)view.findViewById(R.id.rate_text);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = SharedPreference.getUser(getActivity());
        initUi();
        setData(data, salesData);
    }

    private void initUi(){
        CircleData data1 = null, data2 = null;
        if (user.type.equals(User.USER_TYPE_A)) {
            data1 = new CircleData(getString(R.string.distributed_target), "" ,CircleData.TYPE_MONEY);
            data2 = new CircleData(getString(R.string.target_adjust), "", CircleData.TYPE_MONEY);
        } else if (user.type.equals(User.USER_TYPE_B)) {
            data1 = new CircleData(getString(R.string.sales_target_this_month), "",CircleData.TYPE_MONEY);
            data2 = new CircleData(getString(R.string.break_down_target_this_month),"",CircleData.TYPE_MONEY);
        } else if (user.type.equals(User.USER_TYPE_C)) {
            data1 = new CircleData(getString(R.string.sales_target_this_month), "",CircleData.TYPE_MONEY);
        }

        circleView.setData(data1, data2);

        if (SharedPreference.getUser(getActivity()).type.equals(User.USER_TYPE_C)) {
            adjustedLayout.setVisibility(View.GONE);
        }
    }


    public void setData(UserTargetData data, SalesData salesData){
        this.data = data;
        this.salesData = salesData;
        if (targetCompleteText != null && data!= null) {
            //get 3 userTarget

            //set Circle Data
            CircleData data1 = null, data2 = null;
            if (user.type.equals(User.USER_TYPE_A)) {
                data1 = new CircleData(getString(R.string.distributed_target), SharedUtils.addCommaToNum(data.getDistributedTarget()), CircleData.TYPE_MONEY);
                data2 = new CircleData(getString(R.string.target_adjust),  SharedUtils.addCommaToNum("+",data.getAdjust()), CircleData.TYPE_MONEY);
                rateText.setText(R.string.target_complete_rate);
            } else if (user.type.equals(User.USER_TYPE_B)) {
                data1 = new CircleData(getString(R.string.sales_target_this_month), SharedUtils.addCommaToNum(data.getTarget()), CircleData.TYPE_MONEY);
                data2 = new CircleData(getString(R.string.break_down_target_this_month), SharedUtils.addCommaToNum(data.getDecomposed_target()), CircleData.TYPE_MONEY);
                rateText.setText(R.string.indicator_complete_rate);
            } else if (user.type.equals(User.USER_TYPE_C)) {
                data1 = new CircleData(getString(R.string.sales_target_this_month), SharedUtils.addCommaToNum(data.getTarget()), CircleData.TYPE_MONEY);
                rateText.setText(R.string.indicator_complete_rate);
            }

            circleView.setData(data1, data2);

            targetCompleteText.setText(SharedUtils.addCommaToNum(data.getFirstRate(getActivity(),salesData),"%"));
            completeRateText.setText(SharedUtils.addCommaToNum(data.getSecondRate(getActivity(),salesData),"%"));

            startCircleAnim();
        }
    }




    public void startCircleAnim(){
        if (circleView != null) {
            double rate = data.getFirstRate(getActivity(),salesData);
            circleView.startCircleAnim((int)SharedUtils.formatDouble(rate));

        }
    }

}
