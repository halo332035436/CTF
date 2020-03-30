package com.bullb.ctf.PerformanceAssess;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.WebView.MyWebClient;
import com.bumptech.glide.load.Encoder;

import org.altbeacon.range.BTWebView;

import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class PerformAssessmentActivity extends AppCompatActivity {
    private LinearLayout ll;
//    private BTWebView webview;
    private WebView testView;
    private KeyTools keyTools;


    String key = "rnfEKcintpeyoyct";//16

    byte[] initVector;
    String ivString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_checkin_webview);
        setToolbar();
        ll = (LinearLayout)findViewById(R.id.ll);
//        webview = new BTWebView(this);
        testView = new WebView(this);

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 16;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedIVString = buffer.toString();

        Log.d("CBC", "id: "+SharedPreference.getUser(this).id);
        Log.d("CBC", "iv: "+generatedIVString+", "+Base64.encodeToString(generatedIVString.getBytes(), Base64.DEFAULT));
        Log.d("CBC", "key: "+key);


        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        ll.addView(webview, param);
        ll.addView(testView, param);

        String companyCode = "ctf";
        if(BuildConfig.FLAVOR_release_type.equals("CNStaging")||BuildConfig.FLAVOR_release_type.equals("HKStaging")){
            companyCode = "ctfuat";
        }

        String desId = "";
        try {
            desId = KeyTools.performanceDesEncryptId(SharedPreference.getUser(this).id, generatedIVString);
        }catch (Exception e){
            e.printStackTrace();
        }
        decodeCBC(desId, generatedIVString);
        desId = KeyTools.toUrlEncode(desId);

        String ivUrl = Base64.encodeToString(generatedIVString.getBytes(), Base64.DEFAULT);
        ivString = KeyTools.toUrlEncode(ivUrl.replace("\n", "").replace("\r", ""));
        Log.d("WebView cbc", BuildConfig.PERFORM_ASSESS_URL + "?iv="+ivString+"&id="+ desId);
//        webview.setData(BuildConfig.PERFORM_ASSESS_URL + "?iv="+ivString+"&id="+ desId);

        WebSettings settings = testView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);


        testView.setWebViewClient(new MyWebClient(null, testView));
        testView.setBackgroundColor(0);
        testView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        testView.getSettings().setBuiltInZoomControls(true);
        testView.getSettings().setDisplayZoomControls(false);


        testView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0 && keyCode == 4) {
                    if (testView.canGoBack()) {
                        testView.goBack();
                    } else {
                        finish();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });


        Map<String,String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("x-client-id", SharedPreference.getUUID(this));
        extraHeaders.put("x-ctf-app-id", "ctf-smart-talent");
        extraHeaders.put("x-client-os-platform", "android");
        extraHeaders.put("Authorization", "Bearer " + SharedPreference.getToken(this));

        testView.loadUrl(BuildConfig.PERFORM_ASSESS_URL + "?iv="+ivString+"&id="+ desId, extraHeaders);

    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        ImageView doneBtn = (ImageView)findViewById(R.id.done_btn);

        toolbarTitle.setText(getString(R.string.performance_assessment));
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setVisibility(View.GONE);
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.GONE);
        notificationBtn.setVisibility(View.GONE);
        doneBtn.setVisibility(View.GONE);
    }

    private void decodeCBC(String text, String ivString){
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
    protected void onDestroy() {
        super.onDestroy();
//        webview.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        webview.setResult(requestCode, resultCode, data);
    }

}
