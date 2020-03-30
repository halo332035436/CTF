package com.bullb.ctf.Widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bullb.ctf.MenuAdapter;
import com.bullb.ctf.Model.KeyValue;
import com.bullb.ctf.Model.LandingMenuItem;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserRole;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

/**
 * Created by oscarlaw on 5/10/2016.
 */

public class MenuView extends RelativeLayout implements View.OnClickListener{
    private boolean isShow;
    private Point size;
    private RecyclerView recyclerView;
    private ArrayList<LandingMenuItem> menuList;
    private MenuAdapter adapter;
    private ImageView dismissBtn;
    private User user;
    private boolean hasTargetPage = true,
                    hasSalesPerfomancePage = true,
                    hasSaleRankPage= true,
                    hasMybonusPage= true,
                    hasRewardPage= true,
                    hasSaleCampaignPage = true,
                    hasMyTeamPage= true,
                    hasSupervisorScorePage= true;

    public MenuView(Context context) {
        super(context);
        if(!isInEditMode())
            init();
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            init();
    }

    public MenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
            init();
    }

    private void init() {
        inflate(getContext(), R.layout.content_menu, this);
        isShow = false;
        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        ViewHelper.setTranslationY(this, (float)size.y+200);

        //dispatchKeyEventPreIme will work only if layout is in focus
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.requestFocus();
        //disable background control
        this.setOnClickListener(this);

        dismissBtn = (ImageView)findViewById(R.id.dismiss_btn);
        dismissBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        user = SharedPreference.getUser(getContext());
        if (user == null) return;

        menuList = new ArrayList<>();

        UserRole userRole = user.getUserRole();
        ArrayList<KeyValue> keyValues = new ArrayList<>();
        if (userRole != null)
             keyValues = userRole.getDetail();

        if (keyValues != null) {
            for (KeyValue keyValue : keyValues) {
                switch (keyValue.getKey()) {
                    case "target_management":
                        hasTargetPage = keyValue.getValue();
                        break;
                    case "performance_query":
                        hasSalesPerfomancePage = keyValue.getValue();
                        break;
                    case "sale_ranking":
                        hasSaleRankPage = keyValue.getValue();
                        break;
                    case "my_bonus":
                        hasMybonusPage = keyValue.getValue();
                        break;
                    case "marketing_points":
                        hasRewardPage = keyValue.getValue();
                        break;
                    case "sale_campaign":
                        hasSaleCampaignPage = keyValue.getValue();
                        break;
                    case "my_team":
                        hasMyTeamPage = keyValue.getValue();
                        break;
                    case "supervisor_rating":
                        hasSupervisorScorePage = keyValue.getValue();
                        break;
                }
            }
        }
        if (hasTargetPage)
            menuList.add(new LandingMenuItem(R.drawable.target_management, getContext().getString(R.string.target_management), true));
        if (hasSalesPerfomancePage)
            menuList.add(new LandingMenuItem(R.drawable.sales_enquiry, getContext().getString(R.string.performance_enquiry), true));
        if (user.type.equals(User.USER_TYPE_A) && hasSupervisorScorePage)
            menuList.add(new LandingMenuItem(R.drawable.manager_judgement, getContext().getString(R.string.manager_judgement), true));
        if (user.type.equals(User.USER_TYPE_B) && hasMyTeamPage)
            menuList.add(new LandingMenuItem(R.drawable.my_team, getContext().getString(R.string.my_team), true));
        if (hasSaleRankPage)
            menuList.add(new LandingMenuItem(R.drawable.sales_ranking, getContext().getString(R.string.sales_ranking), true));
        if (hasMybonusPage)
            menuList.add(new LandingMenuItem(R.drawable.my_bonus,getContext().getString(R.string.my_bonus), true));
        if (hasRewardPage)
            if(ServerPreference.getServerVersion(getContext()).equals(ServerPreference.SERVER_VERSION_CN)) {
                menuList.add(new LandingMenuItem(R.drawable.sales_mark, getContext().getString(R.string.sales_mark), true));
            }else{
                menuList.add(new LandingMenuItem(R.drawable.sales_mark, getContext().getString(R.string.hk_sales_mark), true));
            }
        if (hasSaleCampaignPage)
            menuList.add(new LandingMenuItem(R.drawable.sales_activity, getContext().getString(R.string.sales_activity), true));
        if(!SharedUtils.appIsHongKong()) {
            menuList.add(new LandingMenuItem(R.drawable.service_evaluation, getContext().getString(R.string.service_evaluation), true));
        }
        menuList.add(new LandingMenuItem(R.drawable.back_menu, getContext().getString(R.string.back_menu), true));

        this.recyclerView = (RecyclerView)findViewById(R.id.menu_recyclerview);

        adapter = new MenuAdapter(getContext(), menuList, (Activity)getContext(), this);
        final GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }


    public void show(){
        this.requestFocus();
        ViewPropertyAnimator.animate(this).translationY(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        isShow = true;
    }

    public void dismissImediately(){
        ViewPropertyAnimator.animate(this).translationY(size.y+200).setDuration(1).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        isShow = false;
    }

    public void dismiss(){
        ViewPropertyAnimator.animate(this).translationY(size.y+200).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        isShow = false;
    }

    public boolean isShow(){
        return isShow;
    }


    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (isShow){
                dismiss();
                return true;
            }
            // do your stuff
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View view) {

    }

}
