package com.bullb.ctf.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.LandingPageActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VerificationActivity extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener{
    private RelativeLayout loadingLayout;
    private TextView errorText;
    private ApiService apiService;
    private Call<BaseResponse> loginTask;

    @Order(1)
    @NotEmpty(messageResId = R.string.error_empty_nid)
    private EditText nidEditText;
    @Order(2)
    @NotEmpty (messageResId = R.string.error_empty_bank_ac)
    private EditText bankAcEditText;
    @Order(3)
    @NotEmpty (messageResId = R.string.error_empty_phone)
    private EditText phoneEditText;

    private Button confirmBtn;
    private Validator validator;
    private KeyTools keyTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        apiService = ServiceGenerator.createService(ApiService.class,this);
        initUi();

        keyTools = KeyTools.getInstance(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        confirmBtn.setOnClickListener(this);
        loadingLayout.setOnClickListener(this);
    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        loadingLayout = (RelativeLayout)findViewById(R.id.loading_layout);
        phoneEditText = (EditText)findViewById(R.id.phone_edit_text);
        bankAcEditText = (EditText)findViewById(R.id.bank_ac_edit_text);
        nidEditText = (EditText)findViewById(R.id.nid_edit_text);
        confirmBtn = (Button)findViewById(R.id.confirm_btn);
        errorText = (TextView)findViewById(R.id.error_msg);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.confirm_btn:
                SharedUtils.hideKeyboard(this, findViewById(R.id.root));
                validator.validate();
                break;
        }
    }

    private void login(){
        errorText.setText("");
        SharedUtils.loading(loadingLayout, true);

        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                login();
            }
        };
        //Generate a new uuid and key pair when login
        Map<String, String> dataMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", getIntent().getStringExtra("id"));
            jsonObject.put("password",getIntent().getStringExtra("password"));
            jsonObject.put("device", SharedPreference.getUUID(this));
            jsonObject.put("bank_account", bankAcEditText.getText().toString());
            jsonObject.put("nid", nidEditText.getText().toString());
            jsonObject.put("phone", phoneEditText.getText().toString());
            jsonObject.put("channel_id", SharedPreference.getChannelId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("data json", jsonObject.toString());
        dataMap = keyTools.encrypt(jsonObject.toString());
        dataMap.put("device_key", keyTools.getPublicKey());

        loginTask = apiService.loginTask("close",dataMap);
        loginTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                SharedUtils.loading(loadingLayout, false);
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv,response.body().data);
                    SharedPreference.setToken(VerificationActivity.this, data);
                    Intent intent = new Intent();
                    intent.setClass(VerificationActivity.this,LandingPageActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else{
                    SharedUtils.handleServerError(VerificationActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                SharedUtils.loading(loadingLayout, false);
                SharedUtils.networkErrorDialogWithRetry(VerificationActivity.this, retry);
            }
        });
    }



    @Override
    public void onValidationSucceeded() {
        login();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        if (errors.size()>0)
            errorText.setText(errors.get(0).getCollatedErrorMessage(this));
    }

    @Override
    protected void onDestroy() {
        if (loginTask != null){
            loginTask.cancel();
        }
        super.onDestroy();
    }
}
