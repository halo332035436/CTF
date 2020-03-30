package com.bullb.ctf.PerformanceEnquiry.TypeC;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.DepartmentSalesData;
import com.bullb.ctf.Model.DepartmentTargetData;
import com.bullb.ctf.Model.Parent;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.PerformanceEnquiry.CircleWithTypeAndDataFragment;
import com.bullb.ctf.PerformanceEnquiry.PerformanceEnquiryActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.PagerAdapter;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.BlurCircleView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class PerformanceCRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public  ArrayList<Department> dataList;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Fragment fragment;
    private View headerView;
    private int page = 0;
    private DepartmentTargetData targetData;
    private DepartmentSalesData salesData;
    private Parent parent = new Parent();
    private Ranks ranks = new Ranks();
    private String fromDate;

    public PerformanceCRecyclerViewAdapter(Context context, ArrayList<Department> dataList, Fragment fragment) {
        this.dataList = dataList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.fragment = fragment;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.performance_enquiry_recyclerview_header, parent, false),fragment);

        }
        else if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new ViewHolder(mLayoutInflater.inflate(R.layout.performance_typec_item, parent, false));

        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderViewHolder){
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerView = headerHolder.itemView.findViewById(R.id.header_layout);

            if (targetData != null && salesData != null) {
                for (int i = 0; i < headerHolder.fragments.size(); i++) {
                    final CircleWithTypeAndDataFragment fragment = ((CircleWithTypeAndDataFragment) headerHolder.fragments.get(i));
                    if (i == 0) {
                        CircleData data1 = new CircleData(mContext.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getAmountAll()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(mContext.getString(R.string.profit_amount), SharedUtils.addCommaToNum(salesData.getGrossProfitAll()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, data2, targetData.getGrossProfitAllRate(salesData) , ranks.getAll(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.indicator_complete_rate),SharedUtils.addCommaToNum(targetData.getCompleteRateAll(salesData),"%"),CircleData.TYPE_TEXT);
                                CircleData data2 = new CircleData(mContext.getString(R.string.sales_last_year),SharedUtils.addCommaToNum(salesData.getCompareLastAllRatio(),"%"),CircleData.TYPE_TEXT);
                                blurView.setData(data1,data2);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.show();
                            }
                        });
                    }
                    else if (i==1){
                        CircleData data1 = new CircleData(mContext.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getAmountA()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(mContext.getString(R.string.profit_amount), SharedUtils.addCommaToNum(salesData.getGrossProfitA()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, data2, targetData.getGrossProfitRateA(salesData), ranks.getA(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.indicator_complete_rate),SharedUtils.addCommaToNum(targetData.getCompleteRateA(salesData),"%"),CircleData.TYPE_TEXT);
                                CircleData data2 = new CircleData(mContext.getString(R.string.sales_last_year),SharedUtils.addCommaToNum(salesData.getCompareLastARatio(),"%"),CircleData.TYPE_TEXT);
                                blurView.setData(data1,data2);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.show();
                            }
                        });
                    }
                    else if (i==2){
                        CircleData data1 = new CircleData(mContext.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getAmountF()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(mContext.getString(R.string.profit_amount), SharedUtils.addCommaToNum(salesData.getGrossProfitF()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, data2, targetData.getGrossProfitRateF(salesData), ranks.getF(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.indicator_complete_rate),SharedUtils.addCommaToNum(targetData.getCompleteRateF(salesData),"%"),CircleData.TYPE_TEXT);
                                CircleData data2 = new CircleData(mContext.getString(R.string.sales_last_year),SharedUtils.addCommaToNum(salesData.getCompareLastFRatio(),"%"),CircleData.TYPE_TEXT);
                                blurView.setData(data1,data2);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.show();
                            }
                        });
                    }
                    else if (i==3){
                        CircleData data1 = new CircleData(mContext.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getAmountE()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(mContext.getString(R.string.profit_amount), SharedUtils.addCommaToNum(salesData.getGrossProfitE()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, data2, targetData.getGrossProfitRateE(salesData), ranks.getE(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.indicator_complete_rate),SharedUtils.addCommaToNum(targetData.getCompleteRateE(salesData),"%"),CircleData.TYPE_TEXT);
                                CircleData data2 = new CircleData(mContext.getString(R.string.sales_last_year),SharedUtils.addCommaToNum(salesData.getCompareLastERatio(),"%"),CircleData.TYPE_TEXT);
                                blurView.setData(data1,data2);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.show();
                            }
                        });
                    }
                    else if (i==4){
                        CircleData data1 = new CircleData(mContext.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getAmountM()),CircleData.TYPE_MONEY);
                        CircleData data2 = new CircleData(mContext.getString(R.string.profit_amount), SharedUtils.addCommaToNum(salesData.getGrossProfitM()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, data2, targetData.getGrossProfitRateE(salesData), ranks.getM(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.indicator_complete_rate),SharedUtils.addCommaToNum(targetData.getCompleteRateM(salesData),"%"),CircleData.TYPE_TEXT);
                                CircleData data2 = new CircleData(mContext.getString(R.string.sales_last_year),SharedUtils.addCommaToNum(salesData.getCompareLastMRatio(),"%"),CircleData.TYPE_TEXT);
                                blurView.setData(data1,data2);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.show();
                            }
                        });
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
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            headerHolder.headerPager.setCurrentItem(page,false);
        }
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.bar_gradient));

            final Department department = (Department)dataList.get(position-1);

            DepartmentTargetData departmentTargetData = new DepartmentTargetData(department.department_targets,null,fromDate);
            DepartmentSalesData departmentSalesData = new DepartmentSalesData(department.departmentSales,fromDate);

            Glide.with(mContext).load(R.drawable.logo).dontAnimate().into(viewHolder.image);
            viewHolder.rateText.setText(mContext.getString(R.string.indicator_complete_rate) + " " + SharedUtils.addCommaToNum(departmentSalesData.getSalesCompleteRate(), "%"));
            viewHolder.progressBar.setProgress((int)SharedUtils.formatDouble(departmentSalesData.getSalesCompleteRate()));
            viewHolder.locationText.setText(department.long_description);
            viewHolder.targetText.setText(SharedUtils.addCommaToNum(departmentSalesData.getAmountAll()));

            double growth = departmentSalesData.getGrowthRate();
            if (Double.isInfinite(growth) || Double.isNaN(growth)){
                viewHolder.growthImg.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.growthImg.setVisibility(View.VISIBLE);
            }

            if (growth >= 0){
                Glide.with(mContext).load(R.drawable.up).into(viewHolder.growthImg);
                viewHolder.growthText.setText(SharedUtils.addCommaToNum(growth,"%"));
            }
            else{
                Glide.with(mContext).load(R.drawable.down).into(viewHolder.growthImg);
                viewHolder.growthText.setText(SharedUtils.addCommaToNum(growth*-1,"%"));
            }

            viewHolder.profitText.setText(SharedUtils.addCommaToNum(departmentSalesData.getGrossProfitAll()));

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerformanceEnquiryActivity a = (PerformanceEnquiryActivity)fragment.getActivity();
                    Intent intent = new Intent();
                    intent.setClass(mContext, PerformanceEnquiryActivity.class);
                    intent.putExtra("year",  a.calendarView.getYear());
                    intent.putExtra("month",  a.calendarView.getMonth());
                    intent.putExtra("quarter",  a.calendarView.getQuarter());
                    intent.putExtra("type",  a.calendarView.getType());
                    if (department.getProperties() == 4) {
                        intent.putExtra("view_type", User.USER_TYPE_B);
                    }else{
                        intent.putExtra("view_type", User.USER_TYPE_C);
                    }
                    intent.putExtra("department", new Gson().toJson(department));
                    intent.putExtra("summary", false);
                    mContext.startActivity(intent);
                }
            });
        }
    }


    public void setData(Ranks rank, Parent parent, DepartmentTargetData targetData, DepartmentSalesData salesData, String fromDate){
        if (rank != null){
            this.ranks = rank;
        }
        if (parent != null){
            this.parent = parent;
        }
        if (targetData != null){
            this.targetData = targetData;
        }
        if (salesData != null){
            this.salesData = salesData;
        }
        this.fromDate = fromDate;
        notifyDataSetChanged();
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



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private View view;
        private ImageView image, growthImg;
        private TextView rateText, locationText, growthText, profitText, targetText;

        ViewHolder(View view){
            super(view);
            this.view = view;
            progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
            image = (ImageView)view.findViewById(R.id.photo);
            rateText = (TextView)view.findViewById(R.id.target_rate);
            growthImg = (ImageView)view.findViewById(R.id.growth_img);
            growthText = (TextView)view.findViewById(R.id.growth_rate);
            profitText = (TextView)view.findViewById(R.id.profit_text);
            targetText = (TextView)view.findViewById(R.id.target_text);
            locationText = (TextView)view.findViewById(R.id.location_text);
        }
    }



    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ViewPager headerPager;
        private CircleIndicator circleIndicator;
        final ArrayList<Fragment> fragments = new ArrayList<>();
        private PagerAdapter adapter;

        HeaderViewHolder(View view, Fragment fragment){
            super(view);
            headerPager = (ViewPager)view.findViewById(R.id.header_pager);
            circleIndicator = (CircleIndicator)view.findViewById(R.id.indicator);
            Gson gson = new Gson();

            CircleData data1 = new CircleData(fragment.getString(R.string.total_sales_amount), "",CircleData.TYPE_MONEY);
            CircleData data1_2 = new CircleData(fragment.getString(R.string.profit_amount), "",CircleData.TYPE_MONEY);
            CircleData data2 = new CircleData(fragment.getString(R.string.total_sales_amount), "",CircleData.TYPE_MONEY);
            CircleData data2_2 = new CircleData(fragment.getString(R.string.profit_amount), "",CircleData.TYPE_MONEY);
            CircleData data3 = new CircleData(fragment.getString(R.string.total_sales_amount), "",CircleData.TYPE_MONEY);
            CircleData data3_2 = new CircleData(fragment.getString(R.string.profit_amount), "",CircleData.TYPE_MONEY);
            CircleData data4 = new CircleData(fragment.getString(R.string.total_sales_amount), "",CircleData.TYPE_MONEY);
            CircleData data4_2 = new CircleData(fragment.getString(R.string.profit_amount), "",CircleData.TYPE_MONEY);

//            preset Data
            if (SharedPreference.getUser(fragment.getActivity()).type.equals(User.USER_TYPE_C) ){
                UserTargetData targetData = SharedPreference.getTarget(fragment.getActivity());
                SalesData salesData = SharedPreference.getSales(fragment.getActivity());

                if (targetData != null && salesData!= null){
                    data1 = new CircleData(fragment.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getTotalSales()) ,CircleData.TYPE_MONEY);
                    data2 = new CircleData(fragment.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getSalesA()) ,CircleData.TYPE_MONEY);
                    data3 = new CircleData(fragment.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getSalesF()) ,CircleData.TYPE_MONEY);
                    data4 = new CircleData(fragment.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getSalesE()) ,CircleData.TYPE_MONEY);
                }
            }
            final CircleWithTypeAndDataFragment fragment1 = CircleWithTypeAndDataFragment.newInstance(gson.toJson(data1),gson.toJson(data1_2),0, fragment.getActivity().getString(R.string.target_type_all));
            final CircleWithTypeAndDataFragment fragment2 = CircleWithTypeAndDataFragment.newInstance(gson.toJson(data2),gson.toJson(data2_2),0, fragment.getActivity().getString(R.string.target_type_A));
            final CircleWithTypeAndDataFragment fragment3 = CircleWithTypeAndDataFragment.newInstance(gson.toJson(data3),gson.toJson(data3_2),0, fragment.getActivity().getString(R.string.target_type_F));
            final CircleWithTypeAndDataFragment fragment4 = CircleWithTypeAndDataFragment.newInstance(gson.toJson(data4),gson.toJson(data4_2),0, fragment.getActivity().getString(R.string.target_type_E));
            fragments.add(fragment1);
            fragments.add(fragment2);
            fragments.add(fragment3);
            fragments.add(fragment4);

            // HK Type M
            if (ServerPreference.getServerVersion(fragment.getContext()).equals(ServerPreference.SERVER_VERSION_HK)) {
                CircleData dataM = new CircleData(fragment.getString(R.string.total_sales_amount), "",CircleData.TYPE_MONEY);
                CircleData dataM_2 = new CircleData(fragment.getString(R.string.profit_amount), "",CircleData.TYPE_MONEY);
                if (SharedPreference.getUser(fragment.getActivity()).type.equals(User.USER_TYPE_C) ) {
                    UserTargetData targetData = SharedPreference.getTarget(fragment.getActivity());
                    SalesData salesData = SharedPreference.getSales(fragment.getActivity());
                    if (targetData != null && salesData!= null){
                        dataM = new CircleData(fragment.getString(R.string.total_sales_amount), SharedUtils.addCommaToNum(salesData.getSalesM()) ,CircleData.TYPE_MONEY);
                    }
                }
                final CircleWithTypeAndDataFragment fragmentM = CircleWithTypeAndDataFragment.newInstance(gson.toJson(dataM),gson.toJson(dataM_2),0, fragment.getActivity().getString(R.string.target_type_M));
                fragments.add(fragmentM);
            }

            adapter = new PagerAdapter(fragment.getChildFragmentManager(), fragments);
            headerPager.setAdapter(adapter);
            headerPager.setOffscreenPageLimit(fragments.size());
            circleIndicator.setViewPager(headerPager);
        }
    }
}
