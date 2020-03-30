package com.bullb.ctf.Notification;

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

import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.SelfManagement.SelfManagementActivity;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.WidgetUtils;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    private NotificationRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<String> dataList;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setToolbar();
        user = SharedPreference.getUser(this);
        initUi();


    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        recyclerView = (RecyclerView)findViewById(R.id.notification_recyclerview);

        dataList = new ArrayList<>();
        dataList.add(getString(R.string.activity_summary));
        if (user.type.equals(User.USER_TYPE_A)) {
            dataList.add(getString(R.string.sales_record));
            dataList.add(getString(R.string.refund_noti));
        }
        dataList.add(getString(R.string.push_notification));


        adapter = new NotificationRecyclerViewAdapter(this, dataList);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.notification);
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.GONE);

    }


}
