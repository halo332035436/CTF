package com.bullb.ctf.Notification;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.MyBonus.MyBonusActivity;
import com.bullb.ctf.PerformanceEnquiry.PerformanceEnquiryActivity;
import com.bullb.ctf.PerformanceEnquiry.TypeA.PerformanceDetailActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.SalesEvents.SalesEventsActivity;
import com.bullb.ctf.SalesPoint.SalesPointActivity;
import com.bullb.ctf.Utils.SharedPreference;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by oscar on 18/1/16.
 */
public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static ArrayList<String> dataList;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_ITEM = 1;

    public NotificationRecyclerViewAdapter(Context context, ArrayList<String> dataList) {
        this.dataList = dataList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new ItemViewHolder(mLayoutInflater.inflate(R.layout.notification_item, parent, false));
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.title.setText(dataList.get(position));
            final String type = dataList.get(position);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("click", dataList.get(position));
                    Intent intent = null;
                    if (type.equals(mContext.getString(R.string.activity_summary))){
                        intent = new Intent(mContext, SalesEventsActivity.class);
                    }
                    else if (type.equals(mContext.getString(R.string.sales_record))) {
                        intent = new Intent(mContext, PerformanceDetailActivity.class);
                        intent.putExtra("page_type", "sales");
                        intent.putExtra("user", new Gson().toJson(SharedPreference.getUser(mContext)));
                    }
                    else if (type.equals(mContext.getString(R.string.refund_noti))) {
                        intent = new Intent(mContext, PerformanceDetailActivity.class);
                        intent.putExtra("page_type", "refund");
                        intent.putExtra("user", new Gson().toJson(SharedPreference.getUser(mContext)));
                    }
                    else if (type.equals(mContext.getString(R.string.push_notification))) {
                        intent = new Intent(mContext, PushNotificationActivity.class);
                    }

                    if (intent != null)
                        mContext.startActivity(intent);
                }
            });
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

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView title;
        private RelativeLayout layout;

        ItemViewHolder(View view){
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.notification_title);
            layout = (RelativeLayout)view.findViewById(R.id.notification_item_layout);

        }
    }


}
