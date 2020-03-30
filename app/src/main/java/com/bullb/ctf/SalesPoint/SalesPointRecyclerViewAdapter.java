package com.bullb.ctf.SalesPoint;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.Reward;
import com.bullb.ctf.Model.RewardData;
import com.bullb.ctf.Model.RewardDetail;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CircleView;

import java.util.ArrayList;

/**
 * Created by oscar on 18/1/16.
 */
public class SalesPointRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static ArrayList<RewardDetail> dataList;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEADER = 0;
    private double remaining;



    public SalesPointRecyclerViewAdapter(Context context, ArrayList<RewardDetail> dataList) {
        this.dataList = dataList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER){
            return new HeaderHolder(mLayoutInflater.inflate(R.layout.sales_point_header_item,parent,false));

        } else if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
                return new ViewHolder(mLayoutInflater.inflate(R.layout.sales_point_list_item, parent, false));
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof  HeaderHolder){
            HeaderHolder headerHolder = (HeaderHolder)holder;
            if(ServerPreference.getServerVersion(mContext).equals(ServerPreference.SERVER_VERSION_HK)){
                headerHolder.detailLabel.setText(mContext.getString(R.string.hk_sales_point_detail));
                headerHolder.circleView.setData(new CircleData(mContext.getString(R.string.hk_sales_mark), SharedUtils.addCommaToNum(remaining), CircleData.TYPE_TEXT), null);
            }else{
                headerHolder.circleView.setData(new CircleData(mContext.getString(R.string.point_remaining), SharedUtils.addCommaToNum(remaining), CircleData.TYPE_TEXT), null);
            }
            headerHolder.smallAmountText.setText(mContext.getString(R.string.small_count) + " " + SharedUtils.addCommaToNum(calDetailSum()));
           }
        else if (holder instanceof ViewHolder){
            RewardDetail rewardDetail = dataList.get(position-1);
            ((ViewHolder) holder).dateText.setText(rewardDetail.date.substring(0,10));
            if (rewardDetail.amount>=0){
                ((ViewHolder) holder).pointText.setText("+" + SharedUtils.addCommaToNum(rewardDetail.amount));
            }
            else{
                ((ViewHolder) holder).pointText.setText(SharedUtils.addCommaToNum(rewardDetail.amount));
            }
            ((ViewHolder) holder).titleText.setText(rewardDetail.description);
        }
    }


    private double calDetailSum(){
        double sum = 0;
        for (RewardDetail rewardDetail: dataList){
            sum += rewardDetail.amount;
        }
        return sum;
    }

    public void setRemaining(double remaining){
        this.remaining = remaining;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position ==0){
            return TYPE_HEADER;
        }
        return  TYPE_ITEM;

    }



    @Override
    public int getItemCount() {
        return dataList == null ? 1 : dataList.size()+1;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dateText, pointText, titleText;


        ViewHolder(View view){
            super(view);
            dateText = (TextView)view.findViewById(R.id.date_text);
            pointText = (TextView)view.findViewById(R.id.point_text);
            titleText = (TextView)view.findViewById(R.id.title_text);

        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        private CircleView circleView;
        private TextView smallAmountText, detailLabel;

        HeaderHolder(View view){
            super(view);
            circleView = (CircleView)view.findViewById(R.id.circle_view);
            smallAmountText = (TextView)view.findViewById(R.id.small_amount_text);
            detailLabel = (TextView)view.findViewById(R.id.point_detail_label);

        }
    }


}
