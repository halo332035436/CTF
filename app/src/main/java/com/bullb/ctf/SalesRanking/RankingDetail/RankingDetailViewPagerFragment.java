package com.bullb.ctf.SalesRanking.RankingDetail;

import android.os.Bundle;
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

public class RankingDetailViewPagerFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM4 = "param4";
    private static final String PROFIT_RATE = "PROFIT_RATE";

    private TextView typeText, profitText, rankText, rankTitle;
    private CircleView circleView;
    private boolean isFirst = false;

    private String mParam1;
    private String mParam2;
    private String type;
    private double profitRate;
    private View.OnClickListener listener;


    public RankingDetailViewPagerFragment() {
        // Required empty public constructor
    }

    /**
     * @param circleData1 總銷售額
     * @param circleData2 Should be null
     * @param type e.g. A, F or E
     * @param profitRate 銷售同比
     * @return
     */
    public static RankingDetailViewPagerFragment newInstance(String circleData1, String circleData2, String type, double profitRate) {
        RankingDetailViewPagerFragment fragment = new RankingDetailViewPagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, circleData1);
        args.putString(ARG_PARAM2, circleData2);
        args.putString(ARG_PARAM4, type);
        args.putDouble(PROFIT_RATE, profitRate);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            type = getArguments().getString(ARG_PARAM4);
            profitRate = getArguments().getDouble(PROFIT_RATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ranking_detail_pager_item, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        typeText = (TextView)view.findViewById(R.id.type_text);
        circleView = (CircleView)view.findViewById(R.id.circle_view);
        profitText = (TextView)view.findViewById(R.id.profit_rate_text);
        rankText = (TextView)view.findViewById(R.id.rank);
        rankTitle = (TextView)view.findViewById(R.id.rank_title);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setData(String rank_title, String rank, CircleData data1, CircleData data2, double rate){
        rankTitle.setText(rank_title);
        rankText.setText(rank);

        circleView.setData(data1, data2);

        typeText.setText(type);
        if (listener != null)
            circleView.setOnClickListener(listener);

        profitText.setText(SharedUtils.addCommaToNum(rate, "%"));
        profitRate = rate;
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    public int[] getCircleViewLocation(){
        int[] locations = new int[2];
        circleView.getLocationOnScreen(locations);
        return locations;
    }

    public void startCircleViewAnimation(){
        if (circleView != null)
            circleView.startCircleAnim((int)SharedUtils.formatDouble(profitRate));
    }

    public void setIsFirst(boolean isFirst){
        this.isFirst = isFirst;
    }
}
