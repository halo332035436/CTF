package com.bullb.ctf.Login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.R;

public class TermsActivity extends AppCompatActivity {
    private Button agreeBtn, disagreeBtn;
    private TextView termsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        initUi();
    }



    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        agreeBtn = (Button)findViewById(R.id.agreeBtn);
        disagreeBtn = (Button)findViewById(R.id.disagreeBtn);
        termsTextView = (TextView)findViewById(R.id.termsTextView);

        termsTextView.setText(R.string.content_terms);
        termsTextView.setMovementMethod(new ScrollingMovementMethod());


        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.setClass(TermsActivity.this, VerificationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        disagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



}
