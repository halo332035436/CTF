package com.bullb.ctf.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.LandingPageActivity;
import com.bullb.ctf.Login.TermsActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.LanguageUtils;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.jpush.android.api.JPushInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener{
    private RelativeLayout loadingLayout;
    private int lang;
    private TextView languageText, errorText, forgotPwBtn;
    private ApiService apiService;
    private Call<BaseResponse> simpleLoginTask;
    private Call<ResponseBody> forgetPasswordTask;
    private ImageView imageView;

    @Order(1)
    @NotEmpty(messageResId = R.string.error_empty_staff_id)
    private EditText userNameEditText;
    @Order(2)
    @NotEmpty (messageResId = R.string.error_empty_password)
    private EditText passwordEditText;

    private Button loginBtn;
    private Validator validator;
    private KeyTools keyTools;


    String key = "rnfEKcintpeyoyct";//16

    byte[] initVector;
    String ivString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keyTools = KeyTools.getInstance(this);
         setContentView(R.layout.activity_login);

        apiService = ServiceGenerator.createService(ApiService.class,this);
        initUi();


//        byte[] iv = new byte[16];
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(iv);
//        initVector = iv;
//        String ivString = Base64.encodeToString(iv, Base64.DEFAULT);


//        int leftLimit = 97; // letter 'a'
//        int rightLimit = 122; // letter 'z'
//        int targetStringLength = 16;
//        Random random = new Random();
//        StringBuilder buffer = new StringBuilder(targetStringLength);
//        for (int i = 0; i < targetStringLength; i++) {
//            int randomLimitedInt = leftLimit + (int)
//                    (random.nextFloat() * (rightLimit - leftLimit + 1));
//            buffer.append((char) randomLimitedInt);
//        }
//        String generatedString = buffer.toString();
//        ivString = generatedString;
//
//        Log.d("CBC", "iv: "+generatedString +" " +ivString);
//        testCBC("01");

        validator = new Validator(this);
        validator.setValidationListener(this);
        languageText.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        loadingLayout.setOnClickListener(this);
        forgotPwBtn.setOnClickListener(this);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if(BuildConfig.DEBUG) {
                    Toast.makeText(LoginActivity.this, "Version code: " + String.valueOf(BuildConfig.VERSION_CODE) + "  Version Name:" + BuildConfig.VERSION_NAME + " \nChannel_id:" + SharedPreference.getChannelId(LoginActivity.this) + "\nManufacturer:" + Build.MANUFACTURER
                            + " \nServer url: " + BuildConfig.SERVER_URL, Toast.LENGTH_SHORT).show();
//                }
                return false;
            }
        });

//        UpdateBuilder.create().check();

    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        loadingLayout = (RelativeLayout)findViewById(R.id.loading_layout);
        languageText = (TextView)findViewById(R.id.language_text_view);
        userNameEditText = (EditText)findViewById(R.id.user_id_edit_text);
        passwordEditText = (EditText)findViewById(R.id.password_edit_text);

        loginBtn = (Button)findViewById(R.id.login_btn);
        errorText = (TextView)findViewById(R.id.error_msg);
        forgotPwBtn = (TextView)findViewById(R.id.forgot_pw_btn);
        imageView = (ImageView)findViewById(R.id.logo_image);

        lang = LanguageUtils.getLanguage(this);
        languageText.setPaintFlags(languageText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        languageText.setText(LanguageUtils.getLanguageName(this,lang));

    }



    private void languageBtnClick(){
        new MaterialDialog.Builder(LoginActivity.this)
                .title(R.string.choose_language)
                .items(R.array.language_arr)
                .itemsCallbackSingleChoice(lang, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Locale locale;
                        if (which != lang) {
                            switch (which) {
                                case 0:
                                    locale = Locale.SIMPLIFIED_CHINESE;
                                    break;
                                case 1:
                                    locale = new Locale("zh", "HK");
                                    break;
                                default:
                                    locale = Locale.ENGLISH;
                            }

                            LanguageUtils.changeLocale(LoginActivity.this, locale);
                            LanguageUtils.setLanguage(which, LoginActivity.this);
                            finish();
                            startActivity(getIntent());
                        }
                        return true;
                    }
                })
                .positiveColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .negativeColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .widgetColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();
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
        String uuid = SharedPreference.getUUID(this);

        Map<String, String> dataMap = new HashMap<>();

        Log.d("MyReceiver", "login: "+SharedPreference.getChannelId(this));
        if(SharedPreference.getChannelId(this).isEmpty()){
            String channelId = JPushInterface.getRegistrationID(this);
            if(channelId.isEmpty()) {
                Toast.makeText(LoginActivity.this, "No Channel ID yet", Toast.LENGTH_SHORT).show();
            } else{
                SharedPreference.setChannelId(this,channelId);
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", userNameEditText.getText().toString());
            jsonObject.put("password",passwordEditText.getText().toString());
            jsonObject.put("device", uuid);
            jsonObject.put("channel_id", SharedPreference.getChannelId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("data json", jsonObject.toString());
        dataMap = keyTools.encrypt(jsonObject.toString());
        dataMap.put("device_key", keyTools.getPublicKey());

        simpleLoginTask = apiService.simpleLoginTask("close",dataMap);
        simpleLoginTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                SharedUtils.loading(loadingLayout, false);
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv,response.body().data);
                    SharedPreference.setToken(LoginActivity.this, data);
                    Intent intent = getIntent();
                    intent.setClass(LoginActivity.this,LandingPageActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
                else if (response.code() == 402) {
                    Intent intent = getIntent();
                    intent.putExtra("id", userNameEditText.getText().toString());
                    intent.putExtra("password", passwordEditText.getText().toString());
                    intent.setClass(LoginActivity.this, TermsActivity.class);
                    startActivity(intent);
                }else{
                   SharedUtils.handleServerError(LoginActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                SharedUtils.loading(loadingLayout, false);
                SharedUtils.networkErrorDialogWithRetry(LoginActivity.this, retry);
            }
        });
    }


    private void resetPassword(final String staffId){
        SharedUtils.loading(loadingLayout, true);

        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetPassword(staffId);
            }
        };
        //Generate a new uuid and key pair when login
        Map<String, String> dataMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", staffId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("data json", jsonObject.toString());
        dataMap = keyTools.encrypt(jsonObject.toString());
        dataMap.put("device_key", keyTools.getPublicKey());

        forgetPasswordTask = apiService.forgetPasswordTask(dataMap);
        forgetPasswordTask.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                SharedUtils.loading(loadingLayout, false);
                if (response.isSuccessful()){
                    Toast.makeText(LoginActivity.this, R.string.check_email, Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedUtils.handleServerError(LoginActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                SharedUtils.loading(loadingLayout, false);
                SharedUtils.networkErrorDialogWithRetry(LoginActivity.this, retry);
            }
        });
    }


    private void showInputDialog(){

        new MaterialDialog.Builder(this)
                .title(R.string.reset_password)
                .content(R.string.dialog_message_reset_password)
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .positiveColor(ContextCompat.getColor(this,R.color.text_dark_red))
                .negativeColor(ContextCompat.getColor(this,R.color.text_dark_red))
                .negativeText(getString(R.string.cancel))
                .buttonRippleColor(ContextCompat.getColor(this,R.color.text_dark_red))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SharedUtils.hideKeyboard(LoginActivity.this, dialog.getView());
                        dialog.dismiss();
                    }
                })
                .autoDismiss(false)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        SharedUtils.hideKeyboard(LoginActivity.this, dialog.getView());
                        if (input != null && input.length() >0 ){
                            resetPassword(input.toString());
                            dialog.dismiss();

                        }
                        else{
                            Toast.makeText(LoginActivity.this, R.string.error_empty_staff_id, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.language_text_view:
                languageBtnClick();
                break;
            case R.id.login_btn:
                SharedUtils.hideKeyboard(this, findViewById(R.id.root));
                validator.validate();
                break;
            case R.id.forgot_pw_btn:
                showInputDialog();
                break;
        }
    }

    private void testCBC(String id){
        try {
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            byte[] encrypted = cipher.doFinal(id.getBytes());
            String encryptedText = Base64.encodeToString(encrypted, Base64.DEFAULT);

            Log.d("CBC", "encryted: "+encryptedText);
            decodeCBC(encryptedText);
            String encodedURL = URLEncoder.encode(encryptedText, "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void decodeCBC(String text){
        try {

            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(text, Base64.DEFAULT));
            String decryted = new String(original);

            Log.d("CBC", "decryted: "+decryted);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (simpleLoginTask != null){
            simpleLoginTask.cancel();
        }
        if (forgetPasswordTask != null){
            forgetPasswordTask.cancel();
        }
        super.onDestroy();
    }
}
