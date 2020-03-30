package com.bullb.ctf.TargetManagement.BreakDown;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserTarget;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.TargetManagement.Predict.PredictCommissionActivity;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.MenuView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditBreakDownActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView limitText, userNameText, userPositionText, targetText, lastYearSalesText;
    private RecyclerView recyclerView;
    private EditBreakDownViewAdapter adapter;
    private ArrayList<String> dataList;
    private User user;
    private ApiService apiService;
    private KeyTools keyTools;
    private Call<BaseResponse> getProfileImageTask;
    private Call<BaseResponse> updateTargetTask;

    private UserTargetData userTargetData;
    private SalesData salesData;
    private RelativeLayout loadingLayout;

    private double originDistributedTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_break_down);

        apiService = ServiceGenerator.createService(ApiService.class, this);
        keyTools = KeyTools.getInstance(this);

        user = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        if (user == null) {
            return;
        }

        initUi();
    }

    private void initUi() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        imageView = (ImageView) findViewById(R.id.photo);
        limitText = (TextView) findViewById(R.id.lower_limit_text);
        userNameText = (TextView) findViewById(R.id.user_name_text);
        userPositionText = (TextView) findViewById(R.id.user_position_text);
        targetText = (TextView) findViewById(R.id.target_text);
        lastYearSalesText = (TextView) findViewById(R.id.last_year_sales_text);
        recyclerView = (RecyclerView) findViewById(R.id.breakdown_edit_recyclerview);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);

        setToolbar();
        setBottomView();

        for (UserTarget target : user.user_targets) {
            Log.d("TargetTest", "edit: " + target.type + " -> " + target.manager_addition);
        }

//      set profile pic
        Glide.with(this).load(user.getIconUrl()).placeholder(R.drawable.user_placeholder).dontAnimate().into(imageView);

        userTargetData = new UserTargetData(user.user_targets, getStartDate(Calendar.getInstance()));

        salesData = new SalesData(user.user_sales, getStartDate(Calendar.getInstance()));

        originDistributedTarget = userTargetData.getDistributedTarget();

        progressBar.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.bar_gradient));
//        progressBar.setProgress(70);

        limitText.setText(this.getString(R.string.lower_limit) + " " + SharedUtils.addCommaToNum(userTargetData.getBaseAll()));
        userNameText.setText(user.name);
        userPositionText.setText(user.title);
//        targetText.setText(SharedUtils.addCommaToNum(userTargetData.getMinDistributedTarget()));
        targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));
        lastYearSalesText.setText(SharedUtils.addCommaToNum(salesData.getLastTotalSales()));
//        progressBar.setProgress((int)SharedUtils.formatDouble(userTargetData.getBaseOverDistributedRatio()));
        progressBar.setProgress((int) SharedUtils.formatDouble(userTargetData.getDistributedOverMaxRatio()));
        dataList = new ArrayList<>();
        dataList.add("");
        dataList.add("");
        dataList.add("");
        if (ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_HK)) {
            dataList.add("");
        }

        adapter = new EditBreakDownViewAdapter(this, dataList, userTargetData, salesData);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    public void notifyDataSetChanged(double managerAdditionA, double managerAdditionE, double managerAdditionF, double managerAdditionM) {
        double adjustTotal = managerAdditionA + managerAdditionE + managerAdditionF + managerAdditionM;
        targetText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseAll() + adjustTotal));
        progressBar.setProgress((int) SharedUtils.formatDouble((userTargetData.getBaseAll() + adjustTotal) * 100 / (userTargetData.getMinBaseAll() * 3)));

    }


//    private void getProfileImage(final String userId){
//        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                getProfileImage(userId);
//            }
//        };
//
//        getProfileImageTask.enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                if (!getProfileImageTask.isCanceled()) {
//                    if (response.isSuccessful()) {
//                        byte[] imageByte = keyTools.decryptImage(response.body().iv, response.body().data);
//                        imageCache.addBitmapToMemoryCache(userId, imageByte);
//                        Glide.with(EditBreakDownActivity.this).load(imageByte).dontAnimate().into(imageView);
//                    } else {
//                        SharedUtils.handleServerError(EditBreakDownActivity.this, response);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                if (!getProfileImageTask.isCanceled()) {
//                    SharedUtils.networkErrorDialogWithRetry(EditBreakDownActivity.this, retry);
//                }
//            }
//        });
//    }


    private void updateTarget() {

        if (SharedUtils.serverIsHongKong(this)) {
            UserTarget targetA = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_A, getStartDate(Calendar.getInstance()));
            UserTarget targetF = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_F, getStartDate(Calendar.getInstance()));
            UserTarget targetE = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_E, getStartDate(Calendar.getInstance()));
            if (targetA == null || targetF == null || targetE == null) {
                if (ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_CN)) {
                    Toast.makeText(this, R.string.error_no_next_target, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.error_no_current_target, Toast.LENGTH_SHORT).show();
                }
                return;
            }


            targetA.manager_addition = adapter.getManagerAdditionA();
            targetE.manager_addition = adapter.getManagerAdditionE();
            targetF.manager_addition = adapter.getManagerAdditionF();

            JSONObject objectA = new JSONObject();
            JSONObject objectF = new JSONObject();
            JSONObject objectE = new JSONObject();
            JSONObject objectM = new JSONObject();
            try {
                objectA.put("manager_addition", adapter.getManagerAdditionA());
                objectA.put("id", targetA.id);
                objectF.put("manager_addition", adapter.getManagerAdditionF());
                objectF.put("id", targetF.id);
                objectE.put("manager_addition", adapter.getManagerAdditionE());
                objectE.put("id", targetE.id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray jsonArrays = new JSONArray();
            jsonArrays.put(objectA);
            jsonArrays.put(objectE);
            jsonArrays.put(objectF);

            if (ServerPreference.getServerVersion(EditBreakDownActivity.this).equals(ServerPreference.SERVER_VERSION_HK)) {
                UserTarget targetM = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_M, getStartDate(Calendar.getInstance()));
                targetM.user_addition = adapter.getManagerAdditionM();
                objectM = new JSONObject();
                try {
                    objectM.put("manager_addition", adapter.getManagerAdditionM());
                    objectM.put("id", targetM.id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArrays.put(objectM);

            }

            final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateTarget();
                }
            };

            Map<String, String> dataMap = new HashMap<>();
            JSONObject jsonObject = new JSONObject();

            Log.d("data json", jsonArrays.toString());
            dataMap = keyTools.encrypt(jsonArrays.toString());
            SharedUtils.loading(loadingLayout, true);


            updateTargetTask = apiService.updateTargetTask("Bearer " + SharedPreference.getToken(this), dataMap);
            updateTargetTask.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (!updateTargetTask.isCanceled()) {
                        SharedUtils.loading(loadingLayout, false);
                        if (response.isSuccessful()) {
                            Intent intent = new Intent();
                            intent.putExtra("adjust_a", adapter.getManagerAdditionA());
                            intent.putExtra("adjust_f", adapter.getManagerAdditionF());
                            intent.putExtra("adjust_e", adapter.getManagerAdditionE());
                            intent.putExtra("adjust_m", adapter.getManagerAdditionM());
                            intent.putExtra("userId", user.id);
                            setResult(RESULT_OK, intent);
                            Toast.makeText(EditBreakDownActivity.this, R.string.data_modified, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            SharedUtils.handleServerError(EditBreakDownActivity.this, response);
                        }
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    if (!updateTargetTask.isCanceled()) {
                        SharedUtils.loading(loadingLayout, false);
                        SharedUtils.networkErrorDialogWithRetry(EditBreakDownActivity.this, retry);
                    }
                }
            });
        } else {
            Log.d("TAG", "updateTarget: " + user.name+ "'s A: "+adapter.getManagerAdditionA());
            Intent intent = new Intent();
            intent.putExtra("adjust_a", adapter.getManagerAdditionA());
            intent.putExtra("adjust_f", adapter.getManagerAdditionF());
            intent.putExtra("adjust_e", adapter.getManagerAdditionE());
            intent.putExtra("adjust_m", adapter.getManagerAdditionM());
            intent.putExtra("userId", user.id);
            setResult(RESULT_OK, intent);
            finish();

        }
    }

    private String getStartDate(Calendar cal) {
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);

        if (month.length() < 2) {
            month = "0" + month;
        }
        return year + "-" + month + "-01";
    }


    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout) findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView) findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        ImageView doneBtn = (ImageView) findViewById(R.id.done_btn);

        toolbarTitle.setText(R.string.breakdown_title);
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.GONE);
        doneBtn.setVisibility(View.VISIBLE);
        doneBtn.setOnClickListener(this);
    }

    private void setBottomView() {
        BottomView bottomView = (BottomView) findViewById(R.id.bottom_view);
        final MenuView menuView = (MenuView) findViewById(R.id.menu_view);
        bottomView.setMenuView(menuView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.done_btn:
                updateTarget();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (getProfileImageTask != null) {
            getProfileImageTask.cancel();
        }
        if (updateTargetTask != null) {
            updateTargetTask.cancel();
        }
        super.onDestroy();
    }
}
