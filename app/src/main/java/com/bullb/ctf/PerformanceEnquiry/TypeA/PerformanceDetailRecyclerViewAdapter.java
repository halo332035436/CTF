package com.bullb.ctf.PerformanceEnquiry.TypeA;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bullb.ctf.Model.SalesDetail;
import com.bullb.ctf.R;
import com.bullb.ctf.SalesPoint.SalesPointRecyclerViewAdapter;
import com.bullb.ctf.Utils.SharedUtils;

import java.util.ArrayList;

/**
 * Created by oscar on 18/1/16.
 */
public class PerformanceDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<SalesDetail> dataList;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_ITEM = 1;


    public PerformanceDetailRecyclerViewAdapter(Context context, ArrayList<SalesDetail> dataList) {
        this.dataList = dataList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
                return new ViewHolder(mLayoutInflater.inflate(R.layout.performance_detail_list_item, parent, false));
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder){
            SalesDetail data = dataList.get(position);
            ((ViewHolder) holder).dateText.setText(data.date.substring(0,10));
            if (data.amount>=0){
                ((ViewHolder) holder).pointText.setText("+" + SharedUtils.addCommaToNum(data.amount));
                ((ViewHolder) holder).statusText.setText(mContext.getString(R.string.sold));
            } else{
                ((ViewHolder) holder).pointText.setText(SharedUtils.addCommaToNum(data.amount));
                ((ViewHolder) holder).statusText.setText(mContext.getString(R.string.reject));
            }
            ((ViewHolder) holder).titleText.setText(data.type);
        }
    }


    @Override
    public int getItemViewType(int position) {
            return  TYPE_ITEM;
    }


    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dateText, pointText, titleText, statusText;
        ViewHolder(View view){
            super(view);
            dateText = (TextView)view.findViewById(R.id.date_text);
            pointText = (TextView)view.findViewById(R.id.point_text);
            titleText = (TextView)view.findViewById(R.id.title_text);
            statusText = (TextView)view.findViewById(R.id.status_text );

        }
    }




}
