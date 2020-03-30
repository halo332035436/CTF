package com.bullb.ctf.SelfManagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.SelfManagementData;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.BarAnimation;
import com.bullb.ctf.Widget.BarView;
import com.bullb.ctf.Widget.CircleView;


public class SalesPerformanceFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private CircleView circleView;
    private int angle;
    private TextView yText;
    private BarView barA,barE, barF, barM;
    private double a,f,e,m, lastA, lastF, lastE, lastM, lastYearSum, sum, rate;

    private User user;

    private SalesData data;


    public SalesPerformanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SalesPerformanceFragment.
     */
    public static SalesPerformanceFragment newInstance(String param1, String param2) {
        SalesPerformanceFragment fragment = new SalesPerformanceFragment();
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
        return inflater.inflate(R.layout.fragment_sales_performance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        circleView = (CircleView) view.findViewById(R.id.circle_view);
        yText = (TextView)view.findViewById(R.id.year_on_year_text);
        barA = (BarView)view.findViewById(R.id.bar_a);
        barE = (BarView)view.findViewById(R.id.bar_e);
        barF = (BarView)view.findViewById(R.id.bar_f);
        barM = (BarView)view.findViewById(R.id.bar_m);

        if (ServerPreference.getServerVersion(getContext()).equals(ServerPreference.SERVER_VERSION_CN)) {
            barM.setVisibility(View.GONE);
            view.findViewById(R.id.label_m).setVisibility(View.GONE);
            view.findViewById(R.id.bar_m_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        user = SharedPreference.getUser(getActivity());
        initUi();
        setData(data);
    }


    private void initUi(){
        CircleData data;
        data = new CircleData(getString(R.string.monthly_sales_performance), "", CircleData.TYPE_MONEY);
        circleView.setData(data, null);
    }

    public void setData(SalesData data){
        this.data = data;
        if (yText != null && data != null) {
            CircleData circleData;
            circleData = new CircleData(getString(R.string.monthly_sales_performance), SharedUtils.addCommaToNum(data.getTotalSales()), CircleData.TYPE_MONEY);
            circleView.setData(circleData, null);
            rate = data.getRate();
            yText.setText(SharedUtils.addCommaToNum(rate,"%"));
            a = data.getSalesA();
            f = data.getSalesF();
            e = data.getSalesE();
            m = data.getSalesM();
            startCircleAnim();
            startBarAnimation();
        }
    }


    public void startBarAnimation(){
        double max = Math.max(Math.max(Math.max(a,f),e),m);
        double aHeight, fHeight, eHeight, mHeight;
        if (max == 0){
            aHeight = 0f;
            fHeight = 0f;
            eHeight = 0f;
            mHeight = 0f;
        }
        else{
            aHeight = a/max;
            fHeight = f/max;
            eHeight = e/max;
            mHeight = m/max;
        }

        barA.setHeightPercentage(0);
        barA.setBarNum(0);
        BarAnimation animation = new BarAnimation(barA, aHeight, a);
        animation.setDuration(1000);
        barA.startAnimation(animation);

        barF.setHeightPercentage(0);
        barF.setBarNum(0);
        BarAnimation animation2 = new BarAnimation(barF, fHeight,f);
        animation2.setDuration(1000);
        barF.startAnimation(animation2);

        barE.setHeightPercentage(0);
        barE.setBarNum(0);
        BarAnimation animation3 = new BarAnimation(barE, eHeight,e);
        animation3.setDuration(1000);
        barE.startAnimation(animation3);

        barM.setHeightPercentage(0);
        barM.setBarNum(0);
        BarAnimation animation4 = new BarAnimation(barM, mHeight,m);
        animation4.setDuration(1000);
        barM.startAnimation(animation4);
    }

    public void startCircleAnim(){
        if (circleView != null) {
            circleView.startCircleAnim((int) SharedUtils.formatDouble(rate));
        }
    }

}
