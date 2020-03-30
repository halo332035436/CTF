package com.bullb.ctf.PerformanceEnquiry.TypeA;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.MenuView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.joooonho.SelectableRoundedImageView;


public class PerformanceDetailActivity extends AppCompatActivity {
    public User user;
    private String userType;
    private TextView userNameText, userPositionText, userPlaceText;
    private SelectableRoundedImageView profileImage;
    private TabLayout tabLayout;

    private int position;
    private String fromDate, toDate;
    private ViewPager pager;
    private String longDescription;
    private TextView contentTypeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_detail);

        user = SharedPreference.getUser(this);

        fromDate = getIntent().getStringExtra("from_date");
        toDate = getIntent().getStringExtra("to_date");

        user = new Gson().fromJson(getIntent().getStringExtra("user"),User.class);
        if (user == null) {
            user = SharedPreference.getUser(this);
        }

        longDescription = getIntent().getStringExtra("long_des");
        if (longDescription == null){
            longDescription = user.getLongDepartmentName();
        }

        userType = getIntent().getStringExtra("view_type");
        if (userType == null || userType.isEmpty()) {
            userType = user.type;
        }
        initUi();
    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        userNameText = (TextView)findViewById(R.id.user_name_text);
        userPositionText = (TextView)findViewById(R.id.user_position_text);
        userPlaceText = (TextView)findViewById(R.id.user_place_text);
        profileImage = (SelectableRoundedImageView)findViewById(R.id.profile_image);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        pager = (ViewPager)findViewById(R.id.performance_detail_viewpager);
        contentTypeText = (TextView)findViewById(R.id.content_type_text);

        setToolbar();
        setBottomView();
        setInfoBanner();
        setTabBar();

        if (getIntent().getStringExtra("page_type") != null && getIntent().getStringExtra("page_type").equals("refund")){
            contentTypeText.setText(R.string.refund_detail);
        }
        else  if (getIntent().getStringExtra("page_type") != null && getIntent().getStringExtra("page_type").equals("sales")){
            contentTypeText.setText(R.string.sales_detail);
        }
        else{
            contentTypeText.setText(R.string.sales_deatil);
        }

    }

    private void setInfoBanner(){
        //set profile pic
        Glide.with(this).load(user.getIconUrl()).placeholder(R.drawable.user_placeholder).dontAnimate().into(profileImage);

        userNameText.setText(user.name);
        userPositionText.setText(user.title);
        userPlaceText.setText(longDescription);
    }


    private void setTabBar(){
        final PerformancePagerAdapter adapter = new PerformancePagerAdapter(getSupportFragmentManager(), this, fromDate, toDate,getIntent().getIntExtra("position", 0),getIntent().getStringExtra("page_type"));
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(adapter.getCount());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((PerformanceDetailFragment)adapter.getItem(position)).setSalesDetails();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        position = getIntent().getIntExtra("position",0);
        // setup tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager);
        if (position != 0)
            pager.setCurrentItem(position);
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        if (getIntent().getStringExtra("page_type") != null && getIntent().getStringExtra("page_type").equals("refund")) {
            toolbarTitle.setText(R.string.refund_noti);
        }
        else if (getIntent().getStringExtra("page_type") != null && getIntent().getStringExtra("page_type").equals("sales")) {
            toolbarTitle.setText(R.string.sales_record);
        }
        else{
            toolbarTitle.setText(R.string.sales_deatil);
        }
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
    }

    private void setBottomView(){
        BottomView bottomView = (BottomView) findViewById(R.id.bottom_view);
        final MenuView menuView = (MenuView) findViewById(R.id.menu_view);
        bottomView.setMenuView(menuView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
