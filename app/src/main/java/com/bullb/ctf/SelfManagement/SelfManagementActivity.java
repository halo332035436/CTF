package com.bullb.ctf.SelfManagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.LandingPageActivity;
import com.bullb.ctf.Model.Banner;
import com.bullb.ctf.Model.BonusesData;
import com.bullb.ctf.Model.Campaign;
import com.bullb.ctf.Model.KeyValue;
import com.bullb.ctf.Model.RewardData;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.SelfManagementData;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.UserRole;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.BannerPagerAdapter;
import com.bullb.ctf.Utils.CampaignPagerAdapter;
import com.bullb.ctf.Utils.CustomViewPager;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.PagerAdapter;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.MenuView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joooonho.SelectableRoundedImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelfManagementActivity extends AppCompatActivity {
    private ImageView bannerImage;
    private SelectableRoundedImageView profileImage;
    private TextView userNameText, userPositionText, userPlaceText;
    private RelativeLayout profileImageLayout;
    private ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private PagerAdapter adapter;
    private User user;
    private ApiService apiService;
    private Call<BaseResponse> getSelfManagementTask;
    private SelfManagementData selfManagementData;
    private KeyTools keyTools;
    private AVLoadingIndicatorView progress;
    private BonusesData bonuses;
    private RewardData rewardData;
    private SalesData salesData;
    private UserTargetData userTargetData;
    private Call<BaseResponse> getCampaignBannerTask;
    private ProgressBar bannerProgress;
    private boolean hasBonus = true, hasPerformance = true, hasIndicator = true;


    private ViewPager bannerViewPager;
    private CircleIndicator indicator;
    private CampaignPagerAdapter bannerAdapter;
    private ArrayList<Campaign> bannerList = new ArrayList<>();
    private Handler bannerHandler = new Handler();
    private final static int loopMilli = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_management);

        apiService = ServiceGenerator.createService(ApiService.class,this);
        keyTools = KeyTools.getInstance(this);

        setToolbar();
        setBottomView();

        user = SharedPreference.getUser(this);
        if (user == null) return;

        initUi();
        setUIData();
    }

    private void initUi(){
        bannerImage = (ImageView)findViewById(R.id.banner_image);
        profileImage = (SelectableRoundedImageView) findViewById(R.id.profile_image);
        userNameText = (TextView)findViewById(R.id.user_name_text);
        userPositionText = (TextView)findViewById(R.id.user_position_text);
        userPlaceText = (TextView)findViewById(R.id.user_place_text);
        viewPager = (ViewPager)findViewById(R.id.dashboard_viewpager);
        profileImageLayout = (RelativeLayout)findViewById(R.id.profile_image_layout);
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);
        bannerProgress = (ProgressBar)findViewById(R.id.banner_progress);
        bannerViewPager = (ViewPager)findViewById(R.id.banner_view_pager);
        indicator = (CircleIndicator)findViewById(R.id.circle_indicator);

        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        //set banner image height to 16:9
        ViewGroup.LayoutParams viewParams = bannerImage.getLayoutParams();
        viewParams.height = SharedUtils.getCampaignBannerHeight(this);
        int height = viewParams.height;
//        bannerImage.setLayoutParams(viewParams);

//      set profile pic (check image in cache first)
//        byte[] image = imageCache.getBitmapFromMemCache(user.id);
//        if (image!= null) {
        Log.d("icon_path", SharedUtils.getIconPath(this, user.id));
        Glide.with(SelfManagementActivity.this).load(user.getIconUrl()).placeholder(R.drawable.user_placeholder).dontAnimate().into(profileImage);
//        }
//        else{
//            getProfileImage();
//        }

        //getBanner

        getCampaignBanner();


        FrameLayout.LayoutParams profileParmas = (FrameLayout.LayoutParams)profileImageLayout.getLayoutParams();
        profileParmas.setMargins(SharedUtils.dpToPx(16), height - SharedUtils.dpToPx(35),0,0);
        profileImageLayout.setLayoutParams(profileParmas);

    }

    private void setUIData() {
        userNameText.setText(user.name);
        userPositionText.setText(user.title);
        userPlaceText.setText(user.getLongDepartmentName());

//        // set profile pic
//        byte[] image = imageCache.getBitmapFromMemCache(user.id);
//        if (image!= null) {
//            Glide.with(SelfManagementActivity.this).load(image).dontAnimate().into(profileImage);
//        } else {
//            getProfileImage();
//        }

        initViewPager();

        bonuses = SharedPreference.getBonuses(this);
        rewardData = SharedPreference.getRewards(this);
        salesData = SharedPreference.getSales(this);
        userTargetData = SharedPreference.getTarget(this);

        // check data loaded or not (some loaded data will be stored in shared preference)
        if (bonuses == null || rewardData == null || salesData == null|| userTargetData == null){
            getSelfManagement();
        } else {
            setData();
        }
    }

    private void getSelfManagement(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSelfManagement();
            }
        };


        progress.setVisibility(View.VISIBLE);
        getSelfManagementTask = apiService.getSelfManagementTask("Bearer " + SharedPreference.getToken(this));
        getSelfManagementTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv, response.body().data);
                    Gson gson = new Gson();
                    selfManagementData = gson.fromJson(data, SelfManagementData.class);
                    bonuses = new BonusesData(selfManagementData.bonuses);
                    rewardData = new RewardData(selfManagementData.user_rewards, selfManagementData.rewards);
                    salesData = new SalesData(selfManagementData.user_sales, selfManagementData.sale, selfManagementData.last_year_sales, SelfManagementActivity.this,SharedUtils.getFirstDay(Calendar.getInstance()));
                    userTargetData = new UserTargetData(selfManagementData.user_targets, selfManagementData.target, selfManagementData.decomposed_target, SharedUtils.getFirstDay(Calendar.getInstance()));
                    //store temp data in shared preference for reuse
                    SharedPreference.setRewards(rewardData, SelfManagementActivity.this);
                    SharedPreference.setSales(salesData, SelfManagementActivity.this);
                    SharedPreference.setBonuses(bonuses, SelfManagementActivity.this);
                    SharedPreference.setTarget(userTargetData, SelfManagementActivity.this);
                    setData();
                }
                else{
                    SharedUtils.handleServerError(SelfManagementActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                SharedUtils.networkErrorDialogWithRetry(SelfManagementActivity.this, retry);
            }
        });
    }


//    private void getCampaignBanner(){
//        bannerProgress.setVisibility(View.VISIBLE);
//        getCampaignBannerTask = apiService.getCampaignBannerTask("Bearer " + SharedPreference.getToken(this));
//        getCampaignBannerTask.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (!getCampaignBannerTask.isCanceled()) {
//                    bannerProgress.setVisibility(View.GONE);
//                    if (response.isSuccessful()) {
//                        Glide.with(SelfManagementActivity.this).load(response.raw().request().url().toString()).into(bannerImage);
//
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                if (!getCampaignBannerTask.isCanceled()) {
//                    bannerProgress.setVisibility(View.GONE);
//                }
//            }
//        });
//    }


    private void getCampaignBanner(){
//        Glide.with(SelfManagementActivity.this).load(R.drawable.advert).into(bannerImage);

        bannerProgress.setVisibility(View.VISIBLE);
        getCampaignBannerTask = apiService.getCampaignBannersTask("Bearer " + SharedPreference.getToken(this));
        getCampaignBannerTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getCampaignBannerTask.isCanceled()) {
                    bannerProgress.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        final Campaign[] banners = new Gson().fromJson(data, Campaign[].class);
                        if(banners.length>0) {
                            bannerViewPager.setVisibility(View.VISIBLE);
                            bannerList.addAll(Arrays.asList(banners));
                            updateBanner();
                        }else{
                            bannerViewPager.setVisibility(View.GONE);
                        }
//                        if (campaign != null)
//                            Glide.with(LandingPageActivity.this).load(campaign.image_url).into(bannerImage);
//                        bannerImage.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent();
//                                intent.putExtra("title",getString(R.string.sales_activity));
//                                intent.putExtra("data",campaign.details);
//                                intent.setClass(LandingPageActivity.this, WebViewActivity.class);
//                                startActivity(intent);
//                            }
//                        });
                    }else{
                        bannerViewPager.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getCampaignBannerTask.isCanceled()) {
                    bannerProgress.setVisibility(View.INVISIBLE);
                }
                bannerViewPager.setVisibility(View.GONE);
            }
        });
    }

    public void setData(){

        for (Fragment fragment : fragments) {
            if (fragment instanceof PrizeFragment) {
                ((PrizeFragment) fragment).setData(bonuses);
            } else if (fragment instanceof SalesPerformanceFragment) {
                ((SalesPerformanceFragment) fragment).setData(salesData);
            } else if (fragment instanceof DashBoardFragment) {
                ((DashBoardFragment) fragment).setData(userTargetData,salesData);
            } else if (fragment instanceof PointRemainFragment) {
                ((PointRemainFragment) fragment).setData(rewardData);
            }
        }
    }

    private void applyRole(){
        UserRole userRole = user.getUserRole();
        ArrayList<KeyValue> keyValues = new ArrayList<>();
        if (userRole != null)
            keyValues = userRole.getDetail();

        if (keyValues != null) {
            for (KeyValue keyValue : keyValues) {
                switch (keyValue.getKey()) {
                    case "index":
                        if(!keyValue.getValue()) {
                            hasBonus = hasIndicator = hasPerformance = false;
                        }
                        break;
                    case "indicator_completion_overview":
                        hasIndicator = keyValue.getValue();
                        break;
                    case "performance_overview":
                        hasPerformance = keyValue.getValue();
                        break;
                    case "bonus_overview":
                        hasBonus = keyValue.getValue();
                        break;

                }
            }
        }
    }



    private void initViewPager(){

        applyRole();

        if(hasIndicator) {
            fragments.add(DashBoardFragment.newInstance("", ""));
        }else{
            fragments.add(EmptyFragment.newInstance("",""));
        }
        if(hasPerformance){
            fragments.add(SalesPerformanceFragment.newInstance("",""));
        }else{
            fragments.add(EmptyFragment.newInstance("",""));
        }
        if(hasBonus){
            fragments.add(PrizeFragment.newInstance("", ""));
        }else{
            fragments.add(EmptyFragment.newInstance("",""));
        }

        if (user.type.equals(User.USER_TYPE_A)) {
            fragments.add(PointRemainFragment.newInstance("",""));
        }
        adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        CircleIndicator indicator = (CircleIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = fragments.get(position);
                if (fragments.get(position) instanceof DashBoardFragment){
                    ((DashBoardFragment)fragment).startCircleAnim();
                } else if (fragments.get(position) instanceof SalesPerformanceFragment){
                    ((SalesPerformanceFragment)fragment).startBarAnimation();
                    ((SalesPerformanceFragment)fragment).startCircleAnim();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
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

    private void updateBanner(){
        bannerAdapter = new CampaignPagerAdapter(SelfManagementActivity.this,bannerList);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        indicator.setViewPager(bannerViewPager);
        bannerHandler.postDelayed(loopBanner,loopMilli);
    }

    private Runnable loopBanner = new Runnable() {
        @Override
        public void run() {
            bannerViewPager.setCurrentItem((bannerViewPager.getCurrentItem()+1)%bannerAdapter.getCount());
            bannerHandler.postDelayed(this,loopMilli);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
//        bannerHandler.removeCallbacks(null);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (getSelfManagementTask != null){
            getSelfManagementTask.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPreference.getTarget(this) != null) {
            userTargetData = SharedPreference.getTarget(this);
        }
        setData();
    }
}
