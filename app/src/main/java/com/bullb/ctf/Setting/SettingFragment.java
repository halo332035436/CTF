package com.bullb.ctf.Setting;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bullb.ctf.Login.LoginActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements View.OnClickListener{
    private RelativeLayout changePwBtn, languageBtn, logoutBtn;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changePwBtn = (RelativeLayout)view.findViewById(R.id.change_pw_btn);
        languageBtn = (RelativeLayout)view.findViewById(R.id.language_btn);
        logoutBtn = (RelativeLayout)view.findViewById(R.id.logout_btn);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changePwBtn.setOnClickListener(this);
        languageBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logout_btn:
                logoutClick();
                break;
            case R.id.change_pw_btn:
                changePwClick();
                break;
            case R.id.language_btn:
                changeLanguageClick();
                break;
        }
    }

    private void changeLanguageClick(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        LanguageFragment fragment = LanguageFragment.newInstance("","");
        fm.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.setting_content_frame, fragment).addToBackStack("").commit();
    }

    private void changePwClick(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ChangePasswordFragment fragment = ChangePasswordFragment.newInstance("","");
        fm.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.setting_content_frame, fragment).addToBackStack("").commit();
    }

    private void logoutClick(){
        SharedUtils.popupDialog(getActivity(), R.string.logout, R.string.alert_logout, R.string.yes, R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreference.logout(getActivity());
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });
    }


}
