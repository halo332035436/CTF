package com.bullb.ctf.SelfManagement;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.TargetManagement.TargetManagementActivity;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.WidgetUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SelfManagementSelectActivity extends AppCompatActivity {
    private SelfManagementSelectRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Department> dataList;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setToolbar();
        user = SharedPreference.getUser(this);
        ArrayList<Department> viewableDepartments = user.getViewable_root_departments();
        if(viewableDepartments.size()==1){
            Intent intent = new Intent();
            intent.setClass(this,TargetManagementActivity.class);
            if(!viewableDepartments.get(0).id.equals(user.getDepartment().id)){
                intent.putExtra("view_type", User.USER_TYPE_C);
            }
            intent.putExtra("department", new Gson().toJson(viewableDepartments.get(0)));
            startActivity(intent);
            finish();
        }
        initUi();


    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        recyclerView = (RecyclerView)findViewById(R.id.notification_recyclerview);

        dataList = new ArrayList<>();
        dataList.addAll(user.getViewable_root_departments());


        adapter = new SelfManagementSelectRecyclerViewAdapter(this, dataList);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView layerBtn = (ImageView)findViewById(R.id.layer_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getResources().getString(R.string.select_layer_label));
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.GONE);
        layerBtn.setVisibility(View.GONE);

    }


}

