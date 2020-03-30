package com.bullb.ctf.SalesEvents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bullb.ctf.Model.Campaign;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.WebView.WebViewActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Campaign> data;
    private Context context;

    public MyRecyclerViewAdapter(ArrayList<Campaign> data, Context context) {
        this.data = data;
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sales_events_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Campaign campaign = data.get(position);
        Glide.with(this.context).load(campaign.image_url).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("title",context.getString(R.string.sales_activity));
                intent.putExtra("n_id",campaign.id);
                intent.putExtra("type","campaign");

                intent.setClass(context, WebViewActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MyViewHolder(View view, Context context) {
            super(view);
            image = (ImageView) view.findViewById(R.id.title);

            Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = SharedUtils.getCampaignBannerHeight(context);

            ViewGroup.LayoutParams params = image.getLayoutParams();
            params.height = height;
            image.setLayoutParams(params);
        }
    }
}
