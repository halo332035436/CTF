package com.bullb.ctf.TargetManagement;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.DepartmentSalesData;
import com.bullb.ctf.Model.DepartmentTargetData;
import com.bullb.ctf.Model.Parent;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.ImageCache;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.PagerAdapter;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.BlurCircleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.HashMap;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TargetRecyclerViewAdapter_B extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Fragment fragment;
    private View headerView;
    private int page = 0;
    private String userType;
    private ArrayList dataList;
    private Ranks ranks = new Ranks();
    private Parent parent = new Parent();
    private DepartmentTargetData targetData;
    private DepartmentSalesData salesData;
    private String departmentLongDes;
    private String departmentShortDes;
    private String fromDate;


    public TargetRecyclerViewAdapter_B(Context context, Fragment fragment, ArrayList dataList, String userType, String departmentLongDes, String departmentShortDes) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.fragment = fragment;
        this.userType = userType;
        this.dataList = dataList;
        this.departmentLongDes = departmentLongDes;
        this.departmentShortDes = departmentShortDes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.target_bc_recyclerview_header, parent, false), fragment, userType);
        }
        else if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            if (userType.equals(User.USER_TYPE_B)){
                return new ItemBViewHolder(mLayoutInflater.inflate(R.layout.target_typeb_item, parent, false));
            }
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder){
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerView = headerHolder.itemView.findViewById(R.id.header_layout);
            if (userType.equals(User.USER_TYPE_B) && targetData != null && salesData != null){
                for (int i = 0 ; i< headerHolder.fragments.size(); i++){
                    final TargetTypeBCFragment fragment = ((TargetTypeBCFragment) headerHolder.fragments.get(i));
                    if (i ==0) {
//                        CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator), SharedUtils.addCommaToNum(targetData.getAmountAll()),CircleData.TYPE_MONEY);
//                        fragment.putData(data1, null,targetData.getCompleteRateAll(salesData), ranks.getAll(), parent.getShortDescription() + mContext.getString(R.string.rank));
//                        fragment.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                                CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator),SharedUtils.addCommaToNum(targetData.getDecomposedTargetAll()),CircleData.TYPE_MONEY);
//                                CircleData data2 = new CircleData(mContext.getString(R.string.breakdown_target_complete_rate), SharedUtils.addCommaToNum(targetData.getCompleteDecomposedRateAll(salesData),"%"),CircleData.TYPE_TEXT);
//                                blurView.setLocation(fragment.getCircleViewLocation());
//                                blurView.setData(data1,data2);
//                                blurView.show();
//                            }
//                        });
                        CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator), SharedUtils.addCommaToNum(targetData.getDecomposedTargetAll()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, null,targetData.getCompleteDecomposedRateAll(salesData), ranks.getAll(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator),SharedUtils.addCommaToNum(targetData.getAmountAll()),CircleData.TYPE_MONEY);
                                CircleData data2 = new CircleData(mContext.getString(R.string.indicator_complete_rate), SharedUtils.addCommaToNum(targetData.getCompleteRateAll(salesData),"%"),CircleData.TYPE_TEXT);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.setData(data1,data2);
                                blurView.show();
                            }
                        });
                    }else if (i ==1) {
//                        CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator), SharedUtils.addCommaToNum(targetData.getAmountAll()),CircleData.TYPE_MONEY);
//                        fragment.putData(data1, null,targetData.getCompleteRateAll(salesData), ranks.getAll(), parent.getShortDescription() + mContext.getString(R.string.rank));
//                        fragment.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                                CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator),SharedUtils.addCommaToNum(targetData.getDecomposedTargetAll()),CircleData.TYPE_MONEY);
//                                CircleData data2 = new CircleData(mContext.getString(R.string.breakdown_target_complete_rate), SharedUtils.addCommaToNum(targetData.getCompleteDecomposedRateAll(salesData),"%"),CircleData.TYPE_TEXT);
//                                blurView.setLocation(fragment.getCircleViewLocation());
//                                blurView.setData(data1,data2);
//                                blurView.show();
//                            }
//                        });
                        CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator), SharedUtils.addCommaToNum(targetData.getDecomposedTargetA()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, null,targetData.getCompleteDecomposedRateA(salesData), ranks.getA(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator),SharedUtils.addCommaToNum(targetData.getAmountA()),CircleData.TYPE_MONEY);
                                CircleData data2 = new CircleData(mContext.getString(R.string.indicator_complete_rate), SharedUtils.addCommaToNum(targetData.getCompleteRateA(salesData),"%"),CircleData.TYPE_TEXT);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.setData(data1,data2);
                                blurView.show();
                            }
                        });
                    }else if(i==2){
//                        CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator), SharedUtils.addCommaToNum(targetData.getAmountF()),CircleData.TYPE_MONEY);
//                        ((TargetTypeBCFragment) headerHolder.fragments.get(i)).putData(data1, null,targetData.getCompleteRateF(salesData), ranks.getF(), parent.getShortDescription() + mContext.getString(R.string.rank));
//                        fragment.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                                CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator),SharedUtils.addCommaToNum(targetData.getDecomposedTargetF()),CircleData.TYPE_MONEY);
//                                CircleData data2 = new CircleData(mContext.getString(R.string.breakdown_target_complete_rate),SharedUtils.addCommaToNum(targetData.getCompleteDecomposedRateF(salesData),"%"),CircleData.TYPE_TEXT);
//                                blurView.setLocation(fragment.getCircleViewLocation());
//                                blurView.setData(data1,data2);
//                                blurView.show();
//                            }
//                        });
                        CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator), SharedUtils.addCommaToNum(targetData.getDecomposedTargetF()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, null,targetData.getCompleteDecomposedRateF(salesData), ranks.getF(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator),SharedUtils.addCommaToNum(targetData.getAmountF()),CircleData.TYPE_MONEY);
                                CircleData data2 = new CircleData(mContext.getString(R.string.indicator_complete_rate), SharedUtils.addCommaToNum(targetData.getCompleteRateF(salesData),"%"),CircleData.TYPE_TEXT);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.setData(data1,data2);
                                blurView.show();
                            }
                        });
                    }else if (i==3){
//                        CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator), SharedUtils.addCommaToNum(targetData.getAmountE()),CircleData.TYPE_MONEY);
//                        ((TargetTypeBCFragment) headerHolder.fragments.get(i)).putData(data1, null,targetData.getCompleteRateE(salesData), ranks.getE(), parent.getShortDescription()+ mContext.getString(R.string.rank));
//                        fragment.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                                CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator),SharedUtils.addCommaToNum(targetData.getDecomposedTargetE()),CircleData.TYPE_MONEY);
//                                CircleData data2 = new CircleData(mContext.getString(R.string.breakdown_target_complete_rate),SharedUtils.addCommaToNum(targetData.getCompleteDecomposedRateE(salesData),"%"),CircleData.TYPE_TEXT);
//                                blurView.setLocation(fragment.getCircleViewLocation());
//                                blurView.setData(data1,data2);
//                                blurView.show();
//                            }
//                        });
                        CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator), SharedUtils.addCommaToNum(targetData.getDecomposedTargetE()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, null,targetData.getCompleteDecomposedRateE(salesData), ranks.getE(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator),SharedUtils.addCommaToNum(targetData.getAmountE()),CircleData.TYPE_MONEY);
                                CircleData data2 = new CircleData(mContext.getString(R.string.indicator_complete_rate), SharedUtils.addCommaToNum(targetData.getCompleteRateE(salesData),"%"),CircleData.TYPE_TEXT);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.setData(data1,data2);
                                blurView.show();
                            }
                        });
                    }else if (i==4){
//                        CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator), SharedUtils.addCommaToNum(targetData.getAmountM()),CircleData.TYPE_MONEY);
//                        ((TargetTypeBCFragment) headerHolder.fragments.get(i)).putData(data1, null,targetData.getCompleteRateM(salesData), ranks.getM(), parent.getShortDescription()+ mContext.getString(R.string.rank));
//                        fragment.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
//                                CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator),SharedUtils.addCommaToNum(targetData.getDecomposedTargetM()),CircleData.TYPE_MONEY);
//                                CircleData data2 = new CircleData(mContext.getString(R.string.breakdown_target_complete_rate),SharedUtils.addCommaToNum(targetData.getCompleteDecomposedRateM(salesData),"%"),CircleData.TYPE_TEXT);
//                                blurView.setLocation(fragment.getCircleViewLocation());
//                                blurView.setData(data1,data2);
//                                blurView.show();
//                            }
//                        });
                        CircleData data1 = new CircleData(mContext.getString(R.string.breakdown_indicator), SharedUtils.addCommaToNum(targetData.getDecomposedTargetM()),CircleData.TYPE_MONEY);
                        fragment.putData(data1, null,targetData.getCompleteDecomposedRateM(salesData), ranks.getM(), parent.getShortDescription() + mContext.getString(R.string.rank));
                        fragment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BlurCircleView blurView = (BlurCircleView)fragment.getActivity().findViewById(R.id.blur_view);
                                CircleData data1 = new CircleData(mContext.getString(R.string.sales_indicator),SharedUtils.addCommaToNum(targetData.getAmountM()),CircleData.TYPE_MONEY);
                                CircleData data2 = new CircleData(mContext.getString(R.string.indicator_complete_rate), SharedUtils.addCommaToNum(targetData.getCompleteRateM(salesData),"%"),CircleData.TYPE_TEXT);
                                blurView.setLocation(fragment.getCircleViewLocation());
                                blurView.setData(data1,data2);
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
                    ((TargetTypeBCFragment)headerHolder.fragments.get(position)).startCircleViewAnimation();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            headerHolder.headerPager.setCurrentItem(page,false);

        }
        else if (holder instanceof ItemBViewHolder) {
            if (userType.equals(User.USER_TYPE_B)) {
                ItemBViewHolder viewHolder = (ItemBViewHolder) holder;
                viewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.bar_gradient));
                final User user = (User)dataList.get(position-1);

                UserTargetData userTargetData = new UserTargetData(user.user_targets, fromDate);
                SalesData salesData = new SalesData(user.user_sales, fromDate);

                Glide.with(mContext)
                        .load(user.getIconUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.user_placeholder))
                        .into(viewHolder.image);

                viewHolder.rateText.setText(mContext.getString(R.string.indicator_complete_rate) + " " + SharedUtils.addCommaToNum(salesData.getSalesCompletionRate(),"%"));
                viewHolder.userNameText.setText(user.name);
                viewHolder.userPositionText.setText(user.title);
                viewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));
                viewHolder.progressBar.setProgress((int)SharedUtils.formatDouble(userTargetData.getDistributedAllRate(salesData)));


                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, TypeAInOtherActivity.class);
                        intent.putExtra("user", new Gson().toJson(user));
                        intent.putExtra("year",  ((TargetManagementFragment)fragment).calendarView.getYear());
                        intent.putExtra("month",  ((TargetManagementFragment)fragment).calendarView.getMonth());
                        intent.putExtra("quarter",  ((TargetManagementFragment)fragment).calendarView.getQuarter());
                        intent.putExtra("type",  ((TargetManagementFragment)fragment).calendarView.getType());
                        intent.putExtra("long_des", departmentLongDes);
                        intent.putExtra("short_des", departmentShortDes);
                        mContext.startActivity(intent);
                    }
                });
            }

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
        if (fromDate != null)
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


    public static class ItemBViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private View view;
        private ImageView image;
        private TextView rateText, userNameText, userPositionText, targetText;

        ItemBViewHolder(View view){
            super(view);
            this.view = view;
            progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
            image = (ImageView)view.findViewById(R.id.photo);
            rateText = (TextView)view.findViewById(R.id.target_rate);
            userNameText = (TextView)view.findViewById(R.id.user_name_text);
            userPositionText = (TextView)view.findViewById(R.id.user_position_text);
            targetText = (TextView)view.findViewById(R.id.target_text);
        }
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ViewPager headerPager;
        private CircleIndicator circleIndicator;
        private ArrayList<Fragment> fragments = new ArrayList<>();
        private PagerAdapter adapter;


        HeaderViewHolder(View view, final Fragment fragment, String userType){
            super(view);
            headerPager = (ViewPager)view.findViewById(R.id.header_pager);
            circleIndicator = (CircleIndicator)view.findViewById(R.id.indicator);

            Gson gson = new Gson();
            if (userType.equals(User.USER_TYPE_B)) {
                CircleData data = new CircleData(fragment.getString(R.string.sales_indicator), "", CircleData.TYPE_MONEY);
                final TargetTypeBCFragment targetTypeBCFragment1 = TargetTypeBCFragment.newInstance(gson.toJson(data), null, 0, fragment.getActivity().getString(R.string.target_type_all), "");
                final TargetTypeBCFragment targetTypeBCFragment2 = TargetTypeBCFragment.newInstance(gson.toJson(data), null, 0, fragment.getActivity().getString(R.string.target_type_A), "");
                final TargetTypeBCFragment targetTypeBCFragment3 = TargetTypeBCFragment.newInstance(gson.toJson(data), null, 0, fragment.getActivity().getString(R.string.target_type_F), "");
                final TargetTypeBCFragment targetTypeBCFragment4 = TargetTypeBCFragment.newInstance(gson.toJson(data), null, 0, fragment.getActivity().getString(R.string.target_type_E), "");
                fragments.add(targetTypeBCFragment1);
                fragments.add(targetTypeBCFragment2);
                fragments.add(targetTypeBCFragment3);
                fragments.add(targetTypeBCFragment4);
                if (ServerPreference.getServerVersion(fragment.getContext()).equals(ServerPreference.SERVER_VERSION_HK)) {
                    final TargetTypeBCFragment targetTypeBCFragmentM = TargetTypeBCFragment.newInstance(gson.toJson(data),null,0,fragment.getActivity().getString(R.string.target_type_M),"");
                    fragments.add(targetTypeBCFragmentM);
                }
            }

            adapter = new PagerAdapter(fragment.getChildFragmentManager(), fragments);
            headerPager.setAdapter(adapter);
            headerPager.setOffscreenPageLimit(fragments.size());
            circleIndicator.setViewPager(headerPager);
        }
    }
}
