package com.bullb.ctf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bullb.ctf.CheckinWebView.CheckInActivity;
import com.bullb.ctf.ManagerRating.ManagerRateActivity;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.MyBonus.MyBonusActivity;
import com.bullb.ctf.MyTeam.MyTeamActivity;
import com.bullb.ctf.PerformanceAssess.PerformAssessmentActivity;
import com.bullb.ctf.SalesEvents.SalesEventsActivity;
import com.bullb.ctf.PerformanceEnquiry.PerformanceEnquiryActivity;
import com.bullb.ctf.SalesPoint.SalesPointActivity;
import com.bullb.ctf.SalesRanking.SalesRankingActivity;
import com.bullb.ctf.SelfManagement.SelfManagementActivity;
import com.bullb.ctf.Model.LandingMenuItem;
import com.bullb.ctf.PersonalInfo.PersonalInfoActivity;
import com.bullb.ctf.ServiceEvaluation.ServiceEvaluationActivity;
import com.bullb.ctf.Setting.SettingActivity;
import com.bullb.ctf.TargetManagement.TargetManagementActivity;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.WebView.CBCWebViewActivity;
import com.bullb.ctf.Widget.MenuView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by oscar on 18/1/16.
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<LandingMenuItem> items;
    private Context mContext;
    private final LayoutInflater mLayoutInflater;
    private Activity activity;
    private MenuView menuView;

    public MenuAdapter(Context context, ArrayList<LandingMenuItem> items, Activity activity, MenuView menuView) {
        this.items = items;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.activity = activity;
        this.menuView = menuView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LandingMenuViewHolder(mLayoutInflater.inflate(R.layout.menu_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final LandingMenuItem item = items.get(position);
        LandingMenuViewHolder viewHolder = (LandingMenuViewHolder) holder;

        Glide.with(mContext).load(item.menuIcon).into(viewHolder.icon);
        viewHolder.titleText.setText(item.menuTitle);
        if (item.enable){
            viewHolder.titleText.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
        }
        else{
            viewHolder.titleText.setTextColor(ContextCompat.getColor(mContext,R.color.disable_grey));
        }

        //Landing Page Menu
        if (activity instanceof LandingPageActivity){
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    if (item.menuTitle.equals(mContext.getString(R.string.self_management))){
                        intent = new Intent(mContext, SelfManagementActivity.class);
//                        intent = new Intent(mContext, SelfManagementSelectActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    }
                    else if (item.menuTitle.equals(mContext.getString(R.string.personal_info))){
                        intent = new Intent(mContext, PersonalInfoActivity.class);
                    }
                    else if (item.menuTitle.equals(mContext.getString(R.string.setting))){
                        intent = new Intent(mContext, SettingActivity.class);
                    }
                    else if (item.menuTitle.equals(mContext.getString(R.string.assessment_management))){
                        intent = new Intent(mContext, CheckInActivity.class);
                    }
                    else if (item.menuTitle.equals(mContext.getString(R.string.performance_assessment))){
                        intent = new Intent(mContext, CBCWebViewActivity.class);
                        intent.putExtra("type", CBCWebViewActivity.PERFORMANCE_ASSESS);
                    }
                    else if (item.menuTitle.equals(mContext.getString(R.string.direct_assessment))){
//                        Bundle arg = new Bundle();
//                        arg.putBoolean("direct", true);
                        intent = new Intent(mContext, CheckInActivity.class);
                        intent.putExtra("direct", true);
                    }
                    else if (item.menuTitle.equals(mContext.getString(R.string.customer_service))){
                        intent = new Intent(mContext, CBCWebViewActivity.class);
                        intent.putExtra("type", CBCWebViewActivity.SEVEN_FISH_QIYU);
                    }
                    if (intent != null){
                        mContext.startActivity(intent);
                    }
                }
            });
        }
        //Main Menu
        else{
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
                    if (item.menuTitle.equals(mContext.getString(R.string.sales_activity))){
                        intent = new Intent(mContext, SalesEventsActivity.class);
                    }
                    else if (item.menuTitle.equals(mContext.getString(R.string.my_bonus))){
                        intent = new Intent(mContext, MyBonusActivity.class);
                    } else if (item.menuTitle.equals(mContext.getString(R.string.target_management))){
                        intent = new Intent(mContext, TargetManagementActivity.class);
                        applyViewableDepartment(intent);
                    } else if (item.menuTitle.equals(mContext.getString(R.string.my_team))) {
                        intent = new Intent(mContext, MyTeamActivity.class);
                    } else if (item.menuTitle.equals(mContext.getString(R.string.performance_enquiry))){
                        intent = new Intent(mContext, PerformanceEnquiryActivity.class);
                        applyViewableDepartment(intent);
                    } else if (item.menuTitle.equals(mContext.getString(R.string.sales_ranking))){
                        intent = new Intent(mContext, SalesRankingActivity.class);
                        applyViewableDepartment(intent);
                    } else if (item.menuTitle.equals(mContext.getString(R.string.manager_judgement))){
                        intent = new Intent(mContext, ManagerRateActivity.class);
                    } else if (item.menuTitle.equals(mContext.getString(R.string.sales_mark))){
                        intent = new Intent(mContext, SalesPointActivity.class);
                    } else if (item.menuTitle.equals(mContext.getString(R.string.hk_sales_mark))){
                        intent = new Intent(mContext, SalesPointActivity.class);
                    } else if(item.menuTitle.equals(mContext.getString(R.string.service_evaluation))){
                        intent = new Intent(mContext, CBCWebViewActivity.class);
                        intent.putExtra("type", CBCWebViewActivity.SERVICE_EVALUTION);
                    } else if (item.menuTitle.equals(mContext.getString(R.string.back_menu))){
                        intent = new Intent(mContext, LandingPageActivity.class);
                        intent.putExtra("no_refresh", true);
                        mContext.startActivity(intent);
                        ((Activity)mContext).finishAffinity();
                        return;
                    }

                    if (intent != null) {
                        activity.startActivity(intent);
                        menuView.dismissImediately();
                    }
                }
            });
        }

    }

    private void applyViewableDepartment(Intent intent){
        User user = SharedPreference.getUser(mContext);
        int viewableIndex = SharedPreference.getViewableRootIndex(mContext);
        viewableIndex = (user.getViewable_root_departments().size()>viewableIndex)?viewableIndex:0;
        if (!user.getViewable_root_departments().get(viewableIndex).id.equals(user.getDepartment().id)) {
            intent.putExtra("view_type", User.USER_TYPE_C);
        }
        intent.putExtra("department", new Gson().toJson(user.getViewable_root_departments().get(viewableIndex)));


    }


    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class LandingMenuViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private ImageView icon;
        private LinearLayout layout;

        LandingMenuViewHolder(View view){
            super(view);
            icon = (ImageView)view.findViewById(R.id.icon);
            titleText = (TextView)view.findViewById(R.id.title_text);
            layout = (LinearLayout)view.findViewById(R.id.menu_item_layout);

            Display display = ((Activity)view.getContext()).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            ViewGroup.LayoutParams viewParams = icon.getLayoutParams();
            viewParams.height = size.x/6;
            viewParams.width = size.x/6;
            icon.setLayoutParams(viewParams);
        }
    }


}
