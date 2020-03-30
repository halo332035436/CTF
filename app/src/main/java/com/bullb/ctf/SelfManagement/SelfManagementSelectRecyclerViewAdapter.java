package com.bullb.ctf.SelfManagement;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.TargetManagement.TargetManagementActivity;
import com.bullb.ctf.Utils.SharedPreference;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SelfManagementSelectRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static ArrayList<Department> dataList;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_ITEM = 1;

    public SelfManagementSelectRecyclerViewAdapter(Context context, ArrayList<Department> dataList) {
        this.dataList = dataList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new ItemViewHolder(mLayoutInflater.inflate(R.layout.select_department_item, parent, false));
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.title.setText(dataList.get(position).long_description);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("click", dataList.get(position).id);
                    Intent intent = new Intent();
                    intent.setClass(mContext,SelfManagementActivity.class);
                    intent.putExtra("department", new Gson().toJson(dataList.get(position)));
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
            title = (TextView) view.findViewById(R.id.department_title);
            layout = (RelativeLayout)view.findViewById(R.id.department_item_layout);

        }
    }


}

