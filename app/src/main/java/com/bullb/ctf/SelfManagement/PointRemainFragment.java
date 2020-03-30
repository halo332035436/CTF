package com.bullb.ctf.SelfManagement;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bullb.ctf.Model.RewardData;
import com.bullb.ctf.Model.SelfManagementData;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CircleView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PointRemainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointRemainFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private CircleView circleView;
    private RewardData data;


    public PointRemainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PointRemainFragment.
     */
    public static PointRemainFragment newInstance(String param1, String param2) {
        PointRemainFragment fragment = new PointRemainFragment();
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
        return inflater.inflate(R.layout.fragment_point_remain, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        circleView = (CircleView) view.findViewById(R.id.circle_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUi();
        setData(data);
    }

    private void initUi(){
        String label;
        if(SharedUtils.appIsHongKong()){
            label = getResources().getString(R.string.hk_sales_mark);
        }else{
            label = getResources().getString(R.string.point_remaining);
        }
        CircleData circleData = new CircleData(label, "", CircleData.TYPE_MONEY);
        circleView.setData(circleData,null);
    }


    public void setData(RewardData data){
        this.data = data;
        if (circleView != null && data != null) {
            String label;
            if(SharedUtils.appIsHongKong()){
                label = getResources().getString(R.string.hk_sales_mark);
            }else{
                label = getResources().getString(R.string.point_remaining);
            }

            CircleData circleData = new CircleData(label, (SharedUtils.addCommaToNum(data.getRewardRemain())), CircleData.TYPE_MONEY);
            circleView.setData(circleData, null);
        }
    }
}
