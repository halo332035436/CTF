package com.bullb.ctf.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bullb.ctf.Model.Banner;
import com.bullb.ctf.R;
import com.bullb.ctf.WebView.WebViewActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BannerPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<Banner> bannerList;

    LayoutInflater inflater;

    public BannerPagerAdapter(Context context, ArrayList<Banner> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView bannerImage;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.banner_image_pager_item,container,false);

        bannerImage = (ImageView) itemView.findViewById(R.id.banner_image);

        Glide.with(context).load(bannerList.get(position).image_url).into(bannerImage);
        bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
//                intent.putExtra("title",bannerList.get(position).name);
                intent.putExtra("type","banner");
                intent.putExtra("url",bannerList.get(position).url);
//                intent.putExtra("data",bannerList.get(position).details);
                intent.setClass(context, WebViewActivity.class);
                if(bannerList.get(position).url!=null) {
                    context.startActivity(intent);
                }
            }
        });

        ((ViewPager)container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}
