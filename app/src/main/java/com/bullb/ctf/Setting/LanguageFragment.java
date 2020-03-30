package com.bullb.ctf.Setting;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bullb.ctf.LandingPageActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.LanguageUtils;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LanguageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LanguageFragment extends Fragment implements View.OnClickListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RelativeLayout simChinBtn, tradChinBtn;


    public LanguageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LanguageFragment.
     */
    public static LanguageFragment newInstance(String param1, String param2) {
        LanguageFragment fragment = new LanguageFragment();
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
        return inflater.inflate(R.layout.fragment_language, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        simChinBtn = (RelativeLayout)view.findViewById(R.id.sim_chin_btn);
        tradChinBtn = (RelativeLayout)view.findViewById(R.id.trad_chin_btn);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        simChinBtn.setOnClickListener(this);
        tradChinBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sim_chin_btn:
                changeLang(LanguageUtils.SIMPLIFIED_CHINESE);
                break;
            case R.id.trad_chin_btn:
                changeLang(LanguageUtils.TRADITIONAL_CHINESE);
                break;

        }
    }

    private void changeLang(int lang){
        Locale locale = null;
        if (lang == LanguageUtils.SIMPLIFIED_CHINESE) {
            locale = new Locale("zh", "CN");
        }
        else if (lang == LanguageUtils.TRADITIONAL_CHINESE) {
            locale = new Locale("zh", "HK");
        }
        LanguageUtils.changeLocale(getActivity(), locale);
        LanguageUtils.setLanguage(lang, getActivity());
        getActivity().finishAffinity();
        Intent intent = new Intent();
        intent.putExtra("is_lang_change",true);
        intent.setClass(getActivity(), LandingPageActivity.class);
        startActivity(intent);
    }
}

