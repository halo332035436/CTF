package com.bullb.ctf.TargetManagement;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bullb.ctf.Model.BonusRate;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.ShareFragment.CircleFragment;
import com.bullb.ctf.R;
import com.bullb.ctf.TargetManagement.Predict.PredictCommissionActivity;
import com.bullb.ctf.Utils.PagerAdapter;
import com.bullb.ctf.Utils.SharedUtils;
import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by oscar on 18/1/16.
 */
public class TargetRecyclerViewAdapter_A extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Fragment fragment;
    private View headerView;
    private int page = 0;
    private String userType;
    private UserTargetData userTargetData;
    private ArrayList dataList;
    private SalesData salesData;
    private BonusRate bonusRate;
    private Ranks ranks;


    public TargetRecyclerViewAdapter_A(Context context, Fragment fragment, ArrayList dataList, String userType, UserTargetData userTargetData, SalesData salesData) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.fragment = fragment;
        this.userType = userType;
        this.userTargetData = userTargetData;
        this.dataList = dataList;
        this.salesData = salesData;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.target_recyclerview_header, parent, false), fragment, userType);
        }
        else if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            if (userType.equals(User.USER_TYPE_A)) {
                return new ItemAViewHolder(mLayoutInflater.inflate(R.layout.target_recyclerview_item, parent, false));
            }
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder){
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerView = headerHolder.itemView.findViewById(R.id.header_layout);
            final Gson gson = new Gson();
            if (userType.equals(User.USER_TYPE_A) && userTargetData != null && salesData != null) {
                for (int i = 0 ; i< headerHolder.fragments.size(); i++){
                    if (i ==0) {
                        CircleData data1 = new CircleData(mContext.getString(R.string.distributed_target), SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()), CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(mContext.getString(R.string.target_adjust), SharedUtils.addCommaToNum("+",userTargetData.getAdjust()),CircleData.TYPE_TEXT);
                        ((CircleFragment) headerHolder.fragments.get(i)).putData(data1, data2);
                    }else if (i ==1){
                        CircleData data3 = new CircleData(fragment.getString(R.string.circle_distributed_target), SharedUtils.addCommaToNum(userTargetData.getFirstRate(mContext,salesData),"%"),CircleData.TYPE_TEXT);
                        CircleData data4 = new CircleData(fragment.getString(R.string.circle_adjusted_complete_rate), SharedUtils.addCommaToNum(userTargetData.getSecondRate(mContext,salesData),"%"),CircleData.TYPE_TEXT);
                        ((CircleFragment) headerHolder.fragments.get(i)).putData(data3, data4, userTargetData.getFirstRate(mContext,salesData));
                    }
                    else if(i==2 && bonusRate != null){
                        CircleData data5 = new CircleData(fragment.getString(R.string.circle_predicted_commission),SharedUtils.addCommaToNum(userTargetData.getPredictCommission(bonusRate)),CircleData.TYPE_TEXT);
                        ((CircleFragment) headerHolder.fragments.get(i)).putData(data5, null);
                        if (SharedUtils.isCurrentMonth(((TargetManagementFragment)fragment).calendarView.getYear(),((TargetManagementFragment)fragment).calendarView.getMonth())) {
                            ((CircleFragment) headerHolder.fragments.get(i)).setCircleViewOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent();
                                    intent.setClass(fragment.getActivity(), PredictCommissionActivity.class);
                                    intent.putExtra("bonus_rate", gson.toJson(bonusRate));
                                    intent.putExtra("target", gson.toJson(userTargetData));
                                    intent.putExtra("sales", gson.toJson(userTargetData));
                                    fragment.startActivityForResult(intent, ((TargetManagementFragment)fragment).PREDICT_COMMISSION_REQUEST);
                                }
                            });
                        }
                        else {
                            ((CircleFragment) headerHolder.fragments.get(i)).setCircleViewOnClickListener(null);
                        }
                    }
                }
            }

            headerHolder.headerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    page = position;
                    ((CircleFragment)headerHolder.fragments.get(position)).startCircleViewAnimation();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            headerHolder.headerPager.setCurrentItem(page,false);

            //set Ranking
            if (userType.equals(User.USER_TYPE_A)) {
                headerHolder.rankTitle.setText(mContext.getString(R.string.rank));
            }
            headerHolder.rankText.setText(TargetManagementFragment.rank);
        }
        else if (holder instanceof ItemAViewHolder) {
            ItemAViewHolder viewHolder = (ItemAViewHolder) holder;
            viewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.bar_gradient));

            if (userType.equals(User.USER_TYPE_A)) {
                //preset data
                if (position == 1) {
                    viewHolder.typeText.setText(R.string.target_type_A);
                }else if (position == 2) {
                    viewHolder.typeText.setText(R.string.target_type_F);
                }else if (position == 3) {
                    viewHolder.typeText.setText(R.string.target_type_E);
                }else if (position == 4) {
                    viewHolder.typeText.setText(R.string.target_type_M);
                }
                //set data
                if (userTargetData != null && salesData != null) {
                    if (position == 1) {
                        viewHolder.progressBar.setProgress((int) userTargetData.getDistributedARate(salesData));
                        viewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_A()));
                        viewHolder.rateText.setText(mContext.getString(R.string.last_year_sales) + " " + SharedUtils.addCommaToNum(salesData.getLastA()));
                    } else if (position == 2) {
                        viewHolder.progressBar.setProgress((int) userTargetData.getDistributedFRate(salesData));
                        viewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_F()));
                        viewHolder.rateText.setText(mContext.getString(R.string.last_year_sales) + " " + SharedUtils.addCommaToNum(salesData.getLastF()));
                    } else if (position == 3 && bonusRate != null) {
                        viewHolder.progressBar.setProgress((int) userTargetData.getDistributedERate(salesData));
                        viewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_E()));
                        viewHolder.rateText.setText(mContext.getString(R.string.last_year_sales) + " " + SharedUtils.addCommaToNum(salesData.getLastE()));
                    } else if (position == 4) {
                        viewHolder.progressBar.setProgress((int) userTargetData.getDistributedMRate(salesData));
                        viewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget_M()));
                        viewHolder.rateText.setText(mContext.getString(R.string.last_year_sales) + " " + SharedUtils.addCommaToNum(salesData.getLastM()));
                    }

                    viewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setClass(mContext, TypeATargetDetailActivity.class);
                            if (position == 1) {
                                intent.putExtra("type", mContext.getString(R.string.target_type_A));
                            } else if (position == 2) {
                                intent.putExtra("type", mContext.getString(R.string.target_type_F));
                            } else if (position == 3) {
                                intent.putExtra("type", mContext.getString(R.string.target_type_E));
                            } else if (position == 4) {
                                intent.putExtra("type", mContext.getString(R.string.target_type_M));
                            }
                            intent.putExtra("year", ((TargetManagementFragment)fragment).calendarView.getYear());
                            intent.putExtra("month", ((TargetManagementFragment)fragment).calendarView.getMonth());
                            intent.putExtra("target", new Gson().toJson(userTargetData));
                            intent.putExtra("sales", new Gson().toJson(salesData));
                            mContext.startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    public void setData(UserTargetData userTargetData, SalesData salesData, BonusRate bonusRate, Ranks ranks){
        this.userTargetData = userTargetData;
        this.salesData = salesData;
        this.bonusRate = bonusRate;
        this.ranks = ranks;
        notifyDataSetChanged();
    }

    public void setData(UserTargetData userTargetData) {
        this.userTargetData = userTargetData;
        notifyDataSetChanged();
    }

    public UserTargetData getUserTargetData() {
        return userTargetData;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_HEADER;
        }
        else {
            return  TYPE_ITEM;
        }
    }



    @Override
    public int getItemCount() {
        return dataList == null ? 1 : dataList.size()+1;
    }

    public void scrollImage(int scrollY){
        ViewHelper.setTranslationY(headerView, (float)(scrollY/3));
    }


    public static class ItemAViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private View view;
        private TextView typeText, targetText,rateText;


        ItemAViewHolder(View view){
            super(view);
            this.view = view;
            progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
            targetText = (TextView)view.findViewById(R.id.target_text);
            typeText = (TextView)view.findViewById(R.id.type_text);
            rateText = (TextView)view.findViewById(R.id.target_rate);
        }
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ViewPager headerPager;
        private CircleIndicator circleIndicator;
        private TextView rankText, rankTitle;
        private ArrayList<Fragment> fragments = new ArrayList<>();
        private PagerAdapter adapter;


        HeaderViewHolder(View view, final Fragment fragment, String userType){
            super(view);
            headerPager = (ViewPager)view.findViewById(R.id.header_pager);
            circleIndicator = (CircleIndicator)view.findViewById(R.id.indicator);
            rankText = (TextView)view.findViewById(R.id.rank);
            rankTitle = (TextView)view.findViewById(R.id.rank_title);

            Gson gson = new Gson();
            if (userType.equals(User.USER_TYPE_A)) {
                CircleData data1 = new CircleData(fragment.getString(R.string.distributed_target),"",CircleData.TYPE_MONEY);
                CircleData data2 = new CircleData(fragment.getString(R.string.target_adjust),"",CircleData.TYPE_TEXT);
                CircleData data3 = new CircleData(fragment.getString(R.string.circle_distributed_target),"",CircleData.TYPE_TEXT);
                CircleData data4 = new CircleData(fragment.getString(R.string.circle_adjusted_complete_rate),"",CircleData.TYPE_TEXT);
                CircleData data5 = new CircleData(fragment.getString(R.string.circle_predicted_commission),"",CircleData.TYPE_TEXT);
                fragments.add(CircleFragment.newInstance(gson.toJson(data1), gson.toJson(data2),0));
                fragments.add(CircleFragment.newInstance(gson.toJson(data3), gson.toJson(data4),0));
                CircleFragment circleFragment = CircleFragment.newInstance(gson.toJson(data5), null,0);
                fragments.add(circleFragment);
            }
            adapter = new PagerAdapter(fragment.getChildFragmentManager(), fragments);
            headerPager.setAdapter(adapter);
            headerPager.setOffscreenPageLimit(fragments.size());
            circleIndicator.setViewPager(headerPager);
        }
    }
}
