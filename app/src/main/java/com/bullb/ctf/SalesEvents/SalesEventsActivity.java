package com.bullb.ctf.SalesEvents;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Banner;
import com.bullb.ctf.Model.Campaign;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.BannerPagerAdapter;
import com.bullb.ctf.Utils.CampaignPagerAdapter;
import com.bullb.ctf.Utils.CustomViewPager;
import com.bullb.ctf.Utils.ImageCache;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.WebView.WebViewActivity;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.MenuView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesEventsActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView bannerImage;
    private Call<BaseResponse> bannerTask;
    private Call<BaseResponse> getCampaignBannerTask;
    private ApiService apiService;
    private ImageCache imageCache;
    private ProgressBar progress;
    private KeyTools keyTools;
    private SearchView searchView;


    private ViewPager bannerViewPager;
    private CircleIndicator indicator;
    private CampaignPagerAdapter bannerAdapter;
    private ArrayList<Campaign> bannerList = new ArrayList<>();

    private Handler bannerHandler = new Handler();
    private final static int loopMilli = 3000;

    FragmentPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_event);

        adapter = new FragmentPagerAdapter(getSupportFragmentManager(), SalesEventsActivity.this);

        apiService = ServiceGenerator.createService(ApiService.class,this);
        imageCache = ImageCache.getInstance();
        keyTools = KeyTools.getInstance(this);

        searchView = (android.support.v7.widget.SearchView) findViewById(R.id.search_view);
        searchView.setActivated(true);
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(searchListener);


        setToolbar();

        initUi();

        //getBanner
        byte[] bannerImageByte = imageCache.getBitmapFromMemCache("banner");
        if (bannerImageByte!= null) {
            Glide.with(SalesEventsActivity.this).load(bannerImageByte).dontAnimate().into(bannerImage);
        }
        else{
            getBanner();
        }

        getCampaignBanner();

    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.sales_activity);
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


    private void updateBanner(){
        bannerAdapter = new CampaignPagerAdapter(SalesEventsActivity.this, bannerList);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        indicator.setViewPager(bannerViewPager);
        bannerHandler.postDelayed(loopBanner,loopMilli);
    }


    private Runnable loopBanner = new Runnable() {
        @Override
        public void run() {
            bannerViewPager.setCurrentItem((bannerViewPager.getCurrentItem()+1)% bannerAdapter.getCount());
            bannerHandler.postDelayed(this,loopMilli);
        }
    };

    private void initUi() {
        // setup viewpager
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        bannerImage = (ImageView)findViewById(R.id.banner_image);
        progress = (ProgressBar)findViewById(R.id.banner_progress);
        searchView = (SearchView)findViewById(R.id.search_view);

        bannerViewPager = (ViewPager)findViewById(R.id.banner_view_pager);
        indicator = (CircleIndicator)findViewById(R.id.circle_indicator);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((PageFragment)adapter.getItem(position)).setCampaigns();
                searchView.setQuery("",false);
                searchView.clearFocus();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // setup tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

//        setBottomView();

        //set Banner Image to 16:9
        ViewGroup.LayoutParams viewParams = bannerImage.getLayoutParams();
        viewParams.height = SharedUtils.getCampaignBannerHeight(this);
        bannerImage.setLayoutParams(viewParams);

    }

    private android.support.v7.widget.SearchView.OnQueryTextListener searchListener = new android.support.v7.widget.SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            //TODO call api to search and update chat list
            return false;
        }

        @Override
        public boolean onQueryTextChange(String q) {
            if(q.isEmpty()){
                ((PageFragment)adapter.getCurrentFragment()).searchCampaign(null);
                return true;
            }
            ((PageFragment)adapter.getCurrentFragment()).searchCampaign(q);
            return true;
        }
    };


//    private void getRandomBanner(){
//        progress.setVisibility(View.VISIBLE);
//        bannerTask = apiService.getRandomBannerTask("Bearer " + SharedPreference.getToken(this));
//        bannerTask.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (!bannerTask.isCanceled()) {
//                    progress.setVisibility(View.GONE);
//                    if (response.isSuccessful()) {
//                        progress.setVisibility(View.GONE);
//                        Glide.with(SalesEventsActivity.this).load(response.raw().request().url().toString()).into(bannerImage);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                if (!bannerTask.isCanceled()) {
//                    progress.setVisibility(View.GONE);
//                }
//            }
//        });
//    }

    private void getCampaignBanner(){
//        Glide.with(LandingPageActivity.this).load(R.drawable.advert).into(bannerImage);

        progress.setVisibility(View.VISIBLE);
        getCampaignBannerTask = apiService.getCampaignBannersTask("Bearer " + SharedPreference.getToken(this));
        getCampaignBannerTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getCampaignBannerTask.isCanceled()) {
                    progress.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        final Campaign[] banners = new Gson().fromJson(data, Campaign[].class);
                        if(banners.length>0) {
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
                    progress.setVisibility(View.INVISIBLE);
                }
                bannerViewPager.setVisibility(View.GONE);
            }
        });
    }

    private void getBanner(){
        bannerTask  = apiService.getNewestCampaignTask("Bearer " + SharedPreference.getToken(this));

        progress.setVisibility(View.VISIBLE);
        bannerTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!bannerTask.isCanceled()){
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        final Campaign campaign = new Gson().fromJson(data, Campaign.class);
                        if (campaign != null)
                            Glide.with(SalesEventsActivity.this).load(campaign.image_url).into(bannerImage);

                        bannerImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.putExtra("title",getString(R.string.sales_activity));
                                intent.putExtra("data",campaign.details);
                                intent.setClass(SalesEventsActivity.this, WebViewActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!bannerTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (bannerTask != null){
            bannerTask.cancel();
        }
        super.onDestroy();
    }
}
