package com.bullb.ctf.SalesRanking.RankingDetail;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bullb.ctf.API.Response.MyTargetPageResponse;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.PagerAdapter;
import com.bullb.ctf.Utils.SharedUtils;
import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class RankingDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private int page = 0;

    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private Fragment fragment;
    private View headerView;
    private MyTargetPageResponse myTarget;
    private String fromDate;

    public RankingDetailRecyclerViewAdapter(Context context, Fragment fragment) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.target_recyclerview_header, parent, false), fragment);
        } else if (viewType == TYPE_ITEM) {
            return new ItemViewHolder(mLayoutInflater.inflate(R.layout.ranking_detail_item, parent, false));
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerView = headerViewHolder.itemView.findViewById(R.id.header_layout);
            if (myTarget != null) {
                setHeaderData(headerViewHolder);
            }
        }
        if (holder instanceof ItemViewHolder)
            if (myTarget != null) {
                setItemData(holder, position);
            }
    }

    private void setHeaderData(HeaderViewHolder headerViewHolder) {
        for (int i = 0; i < headerViewHolder.fragments.size(); i++){
            RankingDetailViewPagerFragment fragment = (RankingDetailViewPagerFragment) headerViewHolder.fragments.get(i);
            Ranks rank = myTarget.ranks;
            String rankTitle = myTarget.department.short_description + mContext.getString(R.string.rank);

            SalesData sd = new SalesData(myTarget.user_sales, fromDate);

            CircleData dataForAll = new CircleData(fragment.getString(R.string.sales_amount), SharedUtils.addCommaToNum(sd.getTotalSales()), CircleData.TYPE_MONEY);
            CircleData dataForA = new CircleData(fragment.getString(R.string.sales_amount), SharedUtils.addCommaToNum(sd.getSalesA()), CircleData.TYPE_MONEY);
            CircleData dataForF = new CircleData(fragment.getString(R.string.sales_amount), SharedUtils.addCommaToNum(sd.getSalesF()), CircleData.TYPE_MONEY);
            CircleData dataForE = new CircleData(fragment.getString(R.string.sales_amount), SharedUtils.addCommaToNum(sd.getSalesE()), CircleData.TYPE_MONEY);
            CircleData dataForM = new CircleData(fragment.getString(R.string.sales_amount),SharedUtils.addCommaToNum(sd.getSalesM()),CircleData.TYPE_MONEY);


            switch (i){
                case 0:
                    fragment.setData(rankTitle, rank.getAll(), dataForAll, null, sd.getCompareLastAllRatio());
                    break;
                case 1:
                    fragment.setData(rankTitle, rank.getA(), dataForA, null, sd.getCompareLastARatio());
                    break;
                case 2:
                    fragment.setData(rankTitle, rank.getF(), dataForF, null, sd.getCompareLastFRatio());
                    break;
                case 3:
                    fragment.setData(rankTitle, rank.getE(), dataForE, null, sd.getCompareLastERatio());
                    break;
                case 4:
                    fragment.setData(rankTitle, rank.getM(), dataForM, null, sd.getCompareLastMRatio());
            }
            // draw the circle animation
            fragment.startCircleViewAnimation();
        }

        page = ((RankingDetailActivity)mContext).getSelectedCategory();
        headerViewHolder.headerPager.setCurrentItem(page, false);
    }

    private void setItemData(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.bar_gradient));

        UserTargetData utd = new UserTargetData(myTarget.user_targets , fromDate);
        SalesData ud = new SalesData(myTarget.user_sales, fromDate);

        switch (position) {
            case 1:
                itemViewHolder.typeText.setText(R.string.target_type_all);
                itemViewHolder.targetText.setText(SharedUtils.addCommaToNum(utd.getDistributedTarget()));
                itemViewHolder.rateText.setText(mContext.getString(R.string.target_completed_percentage) + " " + SharedUtils.addCommaToNum(utd.getDistributedAllRate(ud),"%"));
                itemViewHolder.progressBar.setProgress((int)utd.getDistributedAllRate(ud));
                itemViewHolder.salesLastYearText.setText(mContext.getString(R.string.last_year_sales) + " " + SharedUtils.addCommaToNum(ud.getLastTotalSales()));
                break;
            case 2:
                itemViewHolder.typeText.setText(R.string.target_type_A);
                itemViewHolder.targetText.setText(SharedUtils.addCommaToNum(utd.getDistributedTarget_A()));
                itemViewHolder.rateText.setText(mContext.getString(R.string.target_completed_percentage) + " " + SharedUtils.addCommaToNum(utd.getDistributedARate(ud),"%"));
                itemViewHolder.progressBar.setProgress((int)utd.getDistributedARate(ud));
                itemViewHolder.salesLastYearText.setText(mContext.getString(R.string.last_year_sales) + " " + SharedUtils.addCommaToNum(ud.getLastA()));
                break;
            case 3:
                itemViewHolder.typeText.setText(R.string.target_type_F);
                itemViewHolder.targetText.setText(SharedUtils.addCommaToNum(utd.getDistributedTarget_F()));
                itemViewHolder.rateText.setText(mContext.getString(R.string.target_completed_percentage) + " " + SharedUtils.addCommaToNum(utd.getDistributedFRate(ud),"%"));
                itemViewHolder.progressBar.setProgress((int)utd.getDistributedFRate(ud));
                itemViewHolder.salesLastYearText.setText(mContext.getString(R.string.last_year_sales) + " " + SharedUtils.addCommaToNum(ud.getLastF()));
                break;
            case 4:
                itemViewHolder.typeText.setText(R.string.target_type_E);
                itemViewHolder.targetText.setText(SharedUtils.addCommaToNum(utd.getDistributedTarget_E()));
                itemViewHolder.rateText.setText(mContext.getString(R.string.target_completed_percentage) + " " + SharedUtils.addCommaToNum(utd.getDistributedERate(ud),"%"));
                itemViewHolder.progressBar.setProgress((int)utd.getDistributedERate(ud));
                itemViewHolder.salesLastYearText.setText(mContext.getString(R.string.last_year_sales) + " " + SharedUtils.addCommaToNum(ud.getLastE()));
                break;
            case 5:
                itemViewHolder.typeText.setText(R.string.target_type_M);
                itemViewHolder.targetText.setText(SharedUtils.addCommaToNum(utd.getDistributedTarget_M()));
                itemViewHolder.rateText.setText(mContext.getString(R.string.target_completed_percentage) + " " + SharedUtils.addCommaToNum(utd.getDistributedMRate(ud),"%"));
                itemViewHolder.progressBar.setProgress((int)utd.getDistributedMRate(ud));
                itemViewHolder.salesLastYearText.setText(mContext.getString(R.string.last_year_sales) + " " + SharedUtils.addCommaToNum(ud.getLastM()));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_HEADER;
        } else {
            return  TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        // have header,total, A, F, E, M items
        return 5 + (ServerPreference.getServerVersion(mContext).equals(ServerPreference.SERVER_VERSION_HK)?1:0);
    }

    public void scrollImage(int scrollY){
        ViewHelper.setTranslationY(headerView, (float)(scrollY/3));
    }

    public void setData(MyTargetPageResponse myTarget, String fromDate){
        this.myTarget = myTarget;
        this.fromDate = fromDate;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private PagerAdapter adapter;
        private ViewPager headerPager;
        private CircleIndicator circleIndicator;
        private ArrayList<Fragment> fragments = new ArrayList<>();

        HeaderViewHolder(View view, final Fragment fragment){
            super(view);
            headerPager = (ViewPager)view.findViewById(R.id.header_pager);
            circleIndicator = (CircleIndicator)view.findViewById(R.id.indicator);

            // initialize some default values for fragment to display
            Gson gson = new Gson();
            CircleData dataForAll = new CircleData(fragment.getString(R.string.sales_amount), "", CircleData.TYPE_MONEY);
            CircleData dataForA = new CircleData(fragment.getString(R.string.sales_amount), "", CircleData.TYPE_MONEY);
            CircleData dataForF = new CircleData(fragment.getString(R.string.sales_amount), "", CircleData.TYPE_MONEY);
            CircleData dataForE = new CircleData(fragment.getString(R.string.sales_amount), "", CircleData.TYPE_MONEY);
            CircleData dataForM = new CircleData(fragment.getString(R.string.sales_amount), "", CircleData.TYPE_MONEY);


            fragments.add(RankingDetailViewPagerFragment.newInstance(gson.toJson(dataForAll), null, fragment.getString(R.string.target_type_all), 0.0));
            fragments.add(RankingDetailViewPagerFragment.newInstance(gson.toJson(dataForA), null, fragment.getString(R.string.target_type_A), 0.0));
            fragments.add(RankingDetailViewPagerFragment.newInstance(gson.toJson(dataForF), null, fragment.getString(R.string.target_type_F), 0.0));
            fragments.add(RankingDetailViewPagerFragment.newInstance(gson.toJson(dataForE), null, fragment.getString(R.string.target_type_E), 0.0));
            if(ServerPreference.getServerVersion(mContext).equals(ServerPreference.SERVER_VERSION_HK)){
                fragments.add(RankingDetailViewPagerFragment.newInstance(gson.toJson(dataForM), null, fragment.getString(R.string.target_type_M), 0.0));
            }

            headerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    page = position;
                    ((RankingDetailViewPagerFragment)fragments.get(position)).startCircleViewAnimation();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            adapter = new PagerAdapter(fragment.getChildFragmentManager(), fragments);
            headerPager.setAdapter(adapter);
            headerPager.setOffscreenPageLimit(5);
            circleIndicator.setViewPager(headerPager);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private TextView typeText, targetText,rateText, salesLastYearText;

        ItemViewHolder(View view){
            super(view);
            progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
            targetText = (TextView)view.findViewById(R.id.target_text);
            typeText = (TextView)view.findViewById(R.id.type_text);
            rateText = (TextView)view.findViewById(R.id.target_rate);
            salesLastYearText = (TextView)view.findViewById(R.id.sales_last_year);
        }
    }
}