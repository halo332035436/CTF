package com.bullb.ctf.SelfManagement;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bullb.ctf.Model.BonusesData;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CircleView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrizeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrizeFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView bonusText, commissionText, targetText, profitText;
    private User user;
    private LinearLayout typeCLayout, typeABLayout;
    private double commissionPrize, targetPrize, profit, bonus;
    private BonusesData bonuses;
    private CircleView circleView;


    public PrizeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.«
     * @return A new instance of fragment PrizeFragment.
     */
    public static PrizeFragment newInstance(String param1, String param2) {
        PrizeFragment fragment = new PrizeFragment();
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
        return inflater.inflate(R.layout.fragment_prize, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        commissionText = (TextView)view.findViewById(R.id.commission_price_text);
        targetText = (TextView)view.findViewById(R.id.target_price_text);
        typeCLayout = (LinearLayout)view.findViewById(R.id.type_c_layout);
        typeABLayout = (LinearLayout)view.findViewById(R.id.type_ab_layout);
        profitText = (TextView)view.findViewById(R.id.profit_price_text);
        circleView = (CircleView)view.findViewById(R.id.circle_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUi();
        setData(bonuses);
    }

    private void initUi(){
        user = SharedPreference.getUser(getActivity());
        if (user.type.equals(User.USER_TYPE_C)){
            typeABLayout.setVisibility(View.GONE);
        }
        else{
            typeCLayout.setVisibility(View.GONE);
            typeABLayout.setVisibility(View.VISIBLE);
        }

        //five empty view first load data after api response
        CircleData circleData = new CircleData(getString(R.string.total_bonus), "", CircleData.TYPE_MONEY);
        circleView.setData(circleData,null);
    }

    public void setData(BonusesData bonuses){
        this.bonuses = bonuses;
        if (profitText != null && bonuses != null) {
            commissionText.setText(SharedUtils.addCommaToNum(bonuses.getCommissionPrize()));
            targetText.setText(SharedUtils.addCommaToNum(bonuses.getTargetPrize()));
            profitText.setText(SharedUtils.addCommaToNum(bonuses.getProfitPrize()));

            CircleData circleData = new CircleData(getString(R.string.total_bonus), (SharedUtils.addCommaToNum(bonuses.getTotalBonus(user.type))), CircleData.TYPE_MONEY);
            circleView.setData(circleData, null);
        }
    }
}
