package com.bullb.ctf.Setting;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.R;
import com.bullb.ctf.Utils.WidgetUtils;


public class SettingActivity extends AppCompatActivity{
    private FragmentManager fm;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUi();
    }


    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        imageView = (ImageView)findViewById(R.id.bg_image_view);



        fm = getSupportFragmentManager();
        SettingFragment fragment = SettingFragment.newInstance("","");
        fm.beginTransaction().replace(R.id.setting_content_frame, fragment).commit();

        setToolbar();
        checkIsLangChange();
    }

    private void checkIsLangChange(){
        if (getIntent().getBooleanExtra("is_lang_change",false)){
            LanguageFragment fragment = LanguageFragment.newInstance("","");
            fm.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.setting_content_frame, fragment).addToBackStack("").commit();
        }
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.setting);
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        notificationBtn.setVisibility(View.GONE);
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
    }


}
