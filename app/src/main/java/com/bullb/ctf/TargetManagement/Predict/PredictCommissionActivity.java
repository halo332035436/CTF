package com.bullb.ctf.TargetManagement.Predict;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.BonusRate;
import com.bullb.ctf.Model.UserTarget;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.BottomView;
import com.bullb.ctf.Widget.MenuView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PredictCommissionActivity extends AppCompatActivity implements View.OnClickListener , TextWatcher{
    private TextView distributedNum, adjustedNum, defaultNum, resultNum;
    private EditText inputNum;
    private RecyclerView recyclerview;
    private PredictCommissionViewAdapter adapter;
    private ArrayList<String> dataList;
    private BonusRate bonusRate;
    private double defaultCommission, adjustCommission, totalAdjusted;
    private UserTargetData userTargetData;
    private String beforeText;

    private ApiService apiService;
    private KeyTools keyTools;
    private Call<BaseResponse> updateTargetTask;
    private RelativeLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_commission);

        apiService = ServiceGenerator.createService(ApiService.class, this);
        keyTools = KeyTools.getInstance(this);
        initUi();
    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        distributedNum = (TextView)findViewById(R.id.predict_dist_num);
        adjustedNum = (TextView)findViewById(R.id.predict_adjust_num);
        recyclerview = (RecyclerView)findViewById(R.id.predict_recyclerview);
        defaultNum = (TextView)findViewById(R.id.default_num);
        resultNum = (TextView)findViewById(R.id.result_num);
        inputNum = (EditText)findViewById(R.id.input_num);
        loadingLayout = (RelativeLayout)findViewById(R.id.loading_layout);

        setToolbar();
        setBottomView();

        Gson gson = new Gson();

        userTargetData = gson.fromJson(getIntent().getStringExtra("target"), UserTargetData.class);
        bonusRate = gson.fromJson(getIntent().getStringExtra("bonus_rate"), BonusRate.class);

        defaultCommission = userTargetData.getBaseCommission(bonusRate);
        adjustCommission = userTargetData.getAdditionCommission(bonusRate);
        totalAdjusted = userTargetData.getAdjust();

        distributedNum.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));
        adjustedNum.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget() + totalAdjusted));
        defaultNum.setText(SharedUtils.addCommaToNum(defaultCommission));
        resultNum.setText(SharedUtils.addCommaToNum(getTotalCommission()));
        inputNum.setText(String.valueOf((int)adjustCommission));

        dataList = new ArrayList<>();
        dataList.add("");
        dataList.add("");
        dataList.add("");
        if (ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_HK)) {
            dataList.add("");
        }

        adapter = new PredictCommissionViewAdapter(this, dataList, userTargetData);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(adapter);

        inputNum.requestFocus();
        inputNum.addTextChangedListener(this);

        inputNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputNum.setFocusable(true);
                inputNum.setSelection(inputNum.getText().length());
            }
        });

        inputNum.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(30,2)});

    }


    private void updateTarget(){
        UserTarget targetA = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_A, getStartDate(Calendar.getInstance()));
        UserTarget targetF = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_F, getStartDate(Calendar.getInstance()));
        UserTarget targetE = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_E, getStartDate(Calendar.getInstance()));


        targetA.user_addition = adapter.getAdjustA();
        targetE.user_addition = adapter.getAdjustE();
        targetF.user_addition = adapter.getAdjustF();

        JSONObject objectA = new JSONObject();
        JSONObject objectF = new JSONObject();
        JSONObject objectE = new JSONObject();

        JSONObject objectM = new JSONObject();

        try {
            objectA.put("user_addition", adapter.getAdjustA());
            objectA.put("id", targetA.id);
            objectF.put("user_addition", adapter.getAdjustF());
            objectF.put("id", targetF.id);
            objectE.put("user_addition", adapter.getAdjustE());
            objectE.put("id", targetE.id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jsonArrays = new JSONArray();
        jsonArrays.put(objectA);
        jsonArrays.put(objectE);
        jsonArrays.put(objectF);

        if(ServerPreference.getServerVersion(PredictCommissionActivity.this).equals(ServerPreference.SERVER_VERSION_HK))
        {
            UserTarget targetM = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_M, getStartDate(Calendar.getInstance()));
            targetM.user_addition = adapter.getAdjustM();
            objectM = new JSONObject();
            try {
                objectM.put("user_addition", adapter.getAdjustM());
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

        Log.d("data json",  jsonArrays.toString());
        dataMap = keyTools.encrypt(jsonArrays.toString());
        SharedUtils.loading(loadingLayout, true);

        updateTargetTask = apiService.updateTargetTask("Bearer " + SharedPreference.getToken(this), dataMap);
        updateTargetTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                SharedUtils.loading(loadingLayout, false);
                if (response.isSuccessful()){
                    Intent intent = new Intent();
                    intent.putExtra("adjust_a", adapter.getAdjustA());
                    intent.putExtra("adjust_f", adapter.getAdjustF());
                    intent.putExtra("adjust_e", adapter.getAdjustE());
                    intent.putExtra("adjust_m", adapter.getAdjustM());
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    SharedUtils.handleServerError(PredictCommissionActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                SharedUtils.loading(loadingLayout, false);
                SharedUtils.networkErrorDialogWithRetry(PredictCommissionActivity.this, retry);
            }
        });
    }

    private String getStartDate(Calendar cal) {
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH)+1);

        if (month.length() < 2) {
            month = "0" + month;
        }
        return year + "-" + month + "-01";
    }



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        inputNum.removeTextChangedListener(this);
        if (editable.toString().length() == 0){
            inputNum.setText("0");
        }
        else if (editable.toString().length() > 0 && editable.toString().startsWith(".")){
            inputNum.setText(editable.toString().replace(".",""));
        }
        else if (editable.toString().length() > 1 && editable.toString().startsWith("0")) {
            inputNum.setText(editable.toString().substring(1));
        }
        if (Double.valueOf(inputNum.getText().toString()) > (userTargetData.getBaseCommission(bonusRate)*3 - userTargetData.getBaseCommission(bonusRate))){
            inputNum.setText(String.valueOf((int)SharedUtils.round(userTargetData.getBaseCommission(bonusRate)*3- userTargetData.getBaseCommission(bonusRate),2)));
        }
        inputNum.setSelection(inputNum.getText().length());
        inputNum.addTextChangedListener(this);
        changeInputData(inputNum.getText().toString());
    }


    private void changeInputData(String input){
        double denominator = (userTargetData.getBaseCommission(bonusRate)* 3 -userTargetData.getBaseCommission(bonusRate));
        double ratio = denominator > 0 ? Double.valueOf(input)/denominator : 0;
        double adjustA = ratio * (userTargetData.getDistributedTarget_A()*3 - userTargetData.getDistributedTarget_A());
        double adjustF = ratio * (userTargetData.getDistributedTarget_F()*3 - userTargetData.getDistributedTarget_F());
        double adjustE = ratio * (userTargetData.getDistributedTarget_E()*3 - userTargetData.getDistributedTarget_E());
        double adjustM = ratio * (userTargetData.getDistributedTarget_M()*3 - userTargetData.getDistributedTarget_M());
        adapter.setAdjusted(SharedUtils.round(adjustA,2), SharedUtils.round(adjustF,2), SharedUtils.round(adjustE,2), SharedUtils.round(adjustM, 2));
        totalAdjusted = adjustA + adjustE + adjustF + adjustM;
        adjustCommission = adjustA * bonusRate.getA() + adjustE * bonusRate.getE() + adjustF * bonusRate.getF() + adjustM * bonusRate.getM();
        adjustedNum.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget() + totalAdjusted));
        resultNum.setText(SharedUtils.addCommaToNum(getTotalCommission()));
    }

    private double getTotalCommission(){
        return defaultCommission + adjustCommission;
    }

    public void notifyDataSetChanged(double adjustA, double adjustE, double adjustF, double adjustM){
        totalAdjusted = adjustA + adjustE + adjustF + adjustM;
        adjustCommission = adjustA * bonusRate.getA() + adjustE * bonusRate.getE() + adjustF * bonusRate.getF() + adjustM * bonusRate.getM();
        adjustedNum.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget() + totalAdjusted));
        inputNum.removeTextChangedListener(this);
        inputNum.setText(String.valueOf((int)adjustCommission));
        inputNum.addTextChangedListener(this);
        resultNum.setText(SharedUtils.addCommaToNum(getTotalCommission()));
    }

    public void hideKeyboard(){
        SharedUtils.hideKeyboard(this,inputNum);
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        ImageView doneBtn = (ImageView)findViewById(R.id.done_btn);

        toolbarTitle.setText(R.string.circle_predicted_commission);
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

    private void setBottomView(){
        BottomView bottomView = (BottomView) findViewById(R.id.bottom_view);
        final MenuView menuView = (MenuView) findViewById(R.id.menu_view);
        bottomView.setMenuView(menuView);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.done_btn:
                updateTarget();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (updateTargetTask != null){
            updateTargetTask.cancel();
        }
        super.onDestroy();
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }

}
