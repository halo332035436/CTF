package com.bullb.ctf.CheckinWebView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.WidgetUtils;

import org.altbeacon.range.BTWebView;


public class CheckInActivity extends AppCompatActivity {
    private LinearLayout ll;
    private BTWebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_checkin_webview);
        setToolbar();
        ll = (LinearLayout)findViewById(R.id.ll);
        webview = new BTWebView(this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ll.addView(webview, param);

        String companyCode = "ctf";
        if(BuildConfig.FLAVOR_release_type.equals("CNStaging")||BuildConfig.FLAVOR_release_type.equals("HKStaging")){
            companyCode = "ctfuat";
        }

        String desId = "";
        try {
            desId = KeyTools.attendanceDesEncryptId(SharedPreference.getUser(this).id);
        }catch (Exception e){
            e.printStackTrace();
        }

        boolean direct = getIntent().getBooleanExtra("direct", false);


        Log.d("BTWebView", BuildConfig.GAIA_CHECK_URL + "?companyCode="+companyCode+"&token="+ desId);
        if(direct){
            webview.setData(BuildConfig.GAIA_CHECK_URL + "?companyCode=" + companyCode + "&token=" + desId + "#/signin");
        }else {
            webview.setData(BuildConfig.GAIA_CHECK_URL + "?companyCode=" + companyCode + "&token=" + desId);
        }
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        ImageView doneBtn = (ImageView)findViewById(R.id.done_btn);

        toolbarTitle.setText(getString(R.string.assessment_management));
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setVisibility(View.GONE);
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.GONE);
        notificationBtn.setVisibility(View.GONE);
        doneBtn.setVisibility(View.GONE);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webview.setResult(requestCode, resultCode, data);
    }

}
