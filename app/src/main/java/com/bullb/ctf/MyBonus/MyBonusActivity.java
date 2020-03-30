package com.bullb.ctf.MyBonus;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.Model.Bonus;
import com.bullb.ctf.Model.BonusesData;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.PagerAdapter;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.CalendarView;
import com.bullb.ctf.Widget.MenuView;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyBonusActivity extends AppCompatActivity implements CalendarView.OnCalendarClickListener {
    private CalendarView calendarView;
    private TextView timeText;
    private ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private PagerAdapter adapter;
    private ApiService apiService;
    private Call<BaseResponse> getBonusTask;
    private Call<BaseResponse> recordPageTask;

    private KeyTools keyTools;
    private Calendar cal;
    private User user;
    private AVLoadingIndicatorView progress;
    private Toast noDataToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bonus);
        keyTools = KeyTools.getInstance(this);
        apiService = ServiceGenerator.createService(ApiService.class, this);

        setToolbar();
        setBottomView();
        initUi();

        recordPage("bonus");

        user = SharedPreference.getUser(this);
        if (user == null) return;

        // set UI data
        initViewPager();
        cal = Calendar.getInstance();

        BonusesData data = SharedPreference.getBonuses(this);
        if (data != null) {
            setData(data.getTotalBonus(user.type), data.getTargetPrize(), data.getCommissionPrize(), data.getProfitPrize(), data.getBonuses());
        } else {
            String year = String.valueOf(calendarView.getYear());
            String month = String.valueOf(calendarView.getMonth()+1);
            if (month.length() < 2) month = "0" + month;
            String fromDate = year + "-" + month + "-01";
            getData(fromDate);
        }
    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        calendarView = (CalendarView)findViewById(R.id.calendar_view);
        viewPager = (ViewPager)findViewById(R.id.my_bonus_viewpager);
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);

        calendarView.setCalendarClickListener(this);
    }


    @Override
    public void onBackwardClick() {
        Log.d("debug", "act back");
        monthChange();
    }

    @Override
    public void onForwardClick() {
        Log.d("debug", "act forward");
        monthChange();
    }

    private void monthChange(){
        Log.d("year", String.valueOf(calendarView.getYear() + " " + calendarView.getMonth()));
        String year = String.valueOf(calendarView.getYear());
        String month = String.valueOf(calendarView.getMonth()+1);

        if (month.length() < 2) {
            month = "0" + month;
        }
        String fromDate = year + "-" + month + "-01";
        getData(fromDate);
    }


    private void getData( final String fromDate){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getData(fromDate);
            }
        };

        if (getBonusTask != null)
            getBonusTask.cancel();
        if (noDataToast != null)
            noDataToast.cancel();

        progress.setVisibility(View.VISIBLE);
        final Call<BaseResponse> myBonusTask = apiService.getBonusesTask("Bearer " + SharedPreference.getToken(this),fromDate);

        getBonusTask = myBonusTask;
        myBonusTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!myBonusTask.isCanceled()) {
                    progress.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        BonusesData bonusesData = new Gson().fromJson(data, BonusesData.class);
//                        if (bonusesData.getBonuses() == null || bonusesData.getBonuses().size() == 0) {
//                            noDataToast = Toast.makeText(MyBonusActivity.this, R.string.no_data, Toast.LENGTH_SHORT);
//                            noDataToast.show();
//                        }

                        bonusesData.calculateBonus();
                        setData(bonusesData.getTotalBonus(user.type), bonusesData.getTargetPrize(), bonusesData.getCommissionPrize(), bonusesData.getProfitPrize(), bonusesData.getBonuses());
                    } else {
                        SharedUtils.handleServerError(MyBonusActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!myBonusTask.isCanceled()){
                    progress.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(MyBonusActivity.this, retry);
                }
            }
        });
    }

    private void setData(double total, double target, double commission, double profit, ArrayList<Bonus> bonuses){
       for (int i =0; i<fragments.size();i++){
           CircleData data =null;
           if (i==0){
               data = new CircleData(getString(R.string.total_bonus), SharedUtils.addCommaToNum(total),CircleData.TYPE_MONEY);
           }else if (i==1){
               if (user.type.equals(User.USER_TYPE_C)){
                   data = new CircleData(getBonusByType(bonuses, Bonus.PROFIT_PRIZE).description==null?
                           getString(R.string.profit_prize):getBonusByType(bonuses, Bonus.PROFIT_PRIZE).description,
                           SharedUtils.addCommaToNum(profit), CircleData.TYPE_MONEY);
               }else {
                   data = new CircleData(getBonusByType(bonuses, Bonus.TARGET_COMPLETE_PRIZE).description==null?
                           getString(R.string.target_prize):getBonusByType(bonuses, Bonus.TARGET_COMPLETE_PRIZE).description,
                           SharedUtils.addCommaToNum(target), CircleData.TYPE_MONEY);
               }
           }
           else if (i==2){
                   data = new CircleData(getBonusByType(bonuses, Bonus.COMMISSION_PRIZE).description==null?getString(R.string.commission_prize):getBonusByType(bonuses, Bonus.COMMISSION_PRIZE).description,
                           SharedUtils.addCommaToNum(commission), CircleData.TYPE_MONEY);
           }
           ((BonusFragment)fragments.get(i)).setData(data,null);
       }
    }

    private Bonus getBonusByType(ArrayList<Bonus> bonuses, String type){

        if (bonuses != null) {
            for (Bonus bonus : bonuses) {
                if(bonus.type.equals(type)){
                    return bonus;
                }
            }
        }
        Bonus temp = new Bonus();
        temp.description = null;
        return temp;
    }


    private void initViewPager(){
        Gson gson = new Gson();

        User user = SharedPreference.getUser(this);
        CircleData data1 = new CircleData(getString(R.string.total_bonus),"",CircleData.TYPE_MONEY);
        CircleData data2 = new CircleData(getString(R.string.target_prize),"",CircleData.TYPE_MONEY);

        if (!user.type.equals(User.USER_TYPE_C)){
            CircleData data3 = new CircleData(getString(R.string.commission_prize),"",CircleData.TYPE_MONEY);
            fragments.add(BonusFragment.newInstance(gson.toJson(data1),null));
            fragments.add(BonusFragment.newInstance(gson.toJson(data2), null));
            fragments.add(BonusFragment.newInstance(gson.toJson(data3), null));
        }
        else{
            CircleData data4 = new CircleData(getString(R.string.profit_prize),"",CircleData.TYPE_MONEY);
            fragments.add(BonusFragment.newInstance(gson.toJson(data1),null));
//            fragments.add(BonusFragment.newInstance(gson.toJson(data2), null));
            fragments.add(BonusFragment.newInstance(gson.toJson(data4), null));

        }

        adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        CircleIndicator indicator = (CircleIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
    }


    private void recordPage(String page){
        Map<String, String> dataMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("data json", jsonObject.toString());
        dataMap = keyTools.encrypt(jsonObject.toString());

        recordPageTask = apiService.recordAccessTask("Bearer " + SharedPreference.getToken(MyBonusActivity.this), dataMap);
        recordPageTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
            }
        });
    }


    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.my_bonus);
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


}
