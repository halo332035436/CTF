package com.bullb.ctf.SalesRanking;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bullb.ctf.Model.Descendants;
import com.bullb.ctf.R;

/**
 * Created by oscar on 18/1/16.
 */
public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Descendants[] descendants;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_ITEM = 1;
    private int selected = -1;



    public FilterRecyclerViewAdapter(Context context, Descendants[] descendants) {
        this.descendants = descendants;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new ViewHolder(mLayoutInflater.inflate(R.layout.filter_item, parent, false));

        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.locationText.setText(descendants[position].short_description);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected = position;
                    ((SalesRankingActivity)mContext).setSelectedDepartmentInFilterView(descendants[position]);
                    notifyDataSetChanged();
                }
            });

            if (position == selected){
                viewHolder.locationText.setTextColor(ContextCompat.getColor(mContext, R.color.main_red));
            }
            else{
                viewHolder.locationText.setTextColor(ContextCompat.getColor(mContext, R.color.text_mid_grey));

            }
        }
    }

    public void setSelected(int selected){
        this.selected = selected;
    }

    public int getSelected(){
        return selected;
    }

    @Override
    public int getItemViewType(int position) {
            return  TYPE_ITEM;
    }

    public void setData(Descendants[] descendants){
        this.descendants = descendants;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return descendants == null ? 0 : descendants.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationText;
        private View view;

        ViewHolder(View view){
            super(view);
            this.view = view;
            locationText = (TextView)view.findViewById(R.id.location_text);
        }
    }

}
