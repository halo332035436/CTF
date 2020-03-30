package com.bullb.ctf.TargetManagement;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CircleView;
import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TypeATargetDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TypeATargetDetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM4 = "param4";


    private TextView typeText;
    private CircleView circleView;
    private boolean isFirst = false;

    private String mParam1;
    private String mParam2;
    private String type;
    private Integer progress1, progress2;
    private ProgressBar progressBar1, progressBar2;
    private CircleData data1, data2;

    public TypeATargetDetailFragment() {
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
    public static TypeATargetDetailFragment newInstance(String param1, String param2,String type) {
        TypeATargetDetailFragment fragment = new TypeATargetDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            type = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.target_detail_type_a_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        typeText = (TextView)view.findViewById(R.id.type_text);
        circleView = (CircleView)view.findViewById(R.id.circle_view);
        progressBar1 = (ProgressBar)view.findViewById(R.id.progress_bar1);
        progressBar2 = (ProgressBar)view.findViewById(R.id.progress_bar2);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUi();
    }

    private void initUi(){
        Gson gson = new Gson();
        if (mParam1 != null && data1 == null){
            data1 = gson.fromJson(mParam1,CircleData.class);
        }
        if (mParam2 != null && data2 == null){
            data2 = gson.fromJson(mParam2,CircleData.class);
        }

        circleView.setData(data1,data2);

        typeText.setText(type);

        progressBar1.setProgressDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bar_gradient));
        progressBar1.setProgress(progress1);

        progressBar2.setProgressDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bar_gradient));
        progressBar2.setProgress(progress2);

        startCircleViewAnimation();
    }

    public void setData(CircleData data1, CircleData data2, double rate1, double rate2){
            this.data1 = data1;
            this.data2 = data2;
            this.progress1 = (int) SharedUtils.formatDouble(rate1);
            this.progress2 = (int)SharedUtils.formatDouble(rate2);

        if (circleView != null) {
            circleView.setData(data1, data2);
            progressBar1.setProgress(progress1);
            progressBar2.setProgress(progress2);
            startCircleViewAnimation();

        }
    }



    public int[] getCircleViewLocation(){
        int[] locations = new int[2];
        circleView.getLocationOnScreen(locations);
        return locations;
    }

    public void startCircleViewAnimation(){
        if (circleView != null)
            circleView.startCircleAnim(progress1);
    }

    public void setIsFirst(boolean isFirst){
        this.isFirst = isFirst;
    }


}
