package com.bullb.ctf.SalesRanking;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.Model.LeaderboardData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.SalesRanking.RankingDetail.RankingDetailActivity;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bumptech.glide.Glide;

import java.util.HashMap;

import retrofit2.Call;

public class RankingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;

    private LeaderboardData[] leaderboardList;

    public Call<BaseResponse> imageTask;

    final Context mContext;
    private HashMap<String, String> imageMap = new HashMap<>();
    private LinearLayoutManager layoutManager;
    private final LayoutInflater mLayoutInflater;

    private boolean sortBySale = true;


    public RankingRecyclerViewAdapter(Context context, LinearLayoutManager layoutManager) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.layoutManager = layoutManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new ViewHolder(mLayoutInflater.inflate(R.layout.ranking_item, parent, false));
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.bar_gradient));

            if (leaderboardList[i].name != null && leaderboardList[i].title != null)
                setupUserList(viewHolder, i);
            if (leaderboardList[i].short_description != null && leaderboardList[i].long_description != null)
                setupShopList(viewHolder, i);


            Double d = Double.NaN;
            try {
                d = Double.parseDouble(leaderboardList[i].percentage);
            }catch (Exception e){

            }
            if(sortBySale) {
                viewHolder.targetText.setText(SharedUtils.addCommaToNum(Double.parseDouble(leaderboardList[i].sale)));
            }else{
                if(leaderboardList[i].percentage!=null) {
                    viewHolder.targetText.setText(SharedUtils.round(d, 2) + "%");
                } else{
                    viewHolder.targetText.setText("N/A");
                }
            }
            if (SharedPreference.getUser(mContext).type.equals(User.USER_TYPE_B)||SharedPreference.getUser(mContext).type.equals(User.USER_TYPE_C) )
                viewHolder.rateText.setText(mContext.getString(R.string.indicator_complete_rate) + " " + SharedUtils.addCommaToNum(d,"%"));
            else{
                viewHolder.rateText.setText(mContext.getString(R.string.target_complete_rate) + " " + SharedUtils.addCommaToNum(d,"%"));
            }
        }
    }

    /**
     * Setup layout & data for Users
     * @param viewHolder
     * @param position
     */
    private void setupUserList(ViewHolder viewHolder, final int position) {
        setIcon(viewHolder.image, position);

        viewHolder.titleText.setVisibility(View.GONE);
        viewHolder.userNameText.setVisibility(View.VISIBLE);
        viewHolder.userPositionText.setVisibility(View.VISIBLE);
        viewHolder.nextBtn.setVisibility(View.VISIBLE);
        if(SharedUtils.appIsHongKong()&&SharedPreference.getUser(mContext).type.equals(User.USER_TYPE_A)
                &&!(leaderboardList[position].id.equals(SharedPreference.getUser(mContext).id))){
            viewHolder.userNameText.setText("");
            viewHolder.userPositionText.setText("");
        }else {
            viewHolder.userNameText.setText(leaderboardList[position].name);
            viewHolder.userPositionText.setText(leaderboardList[position].title);
        }
        Double d = Double.NaN;
        try {
            d = Double.parseDouble(leaderboardList[position].percentage);
        }catch (Exception e){
            e.printStackTrace();
        }
        viewHolder.progressBar.setProgress((int)SharedUtils.formatDouble(d));
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RankingDetailActivity.class);
                Integer selectedType = ((SalesRankingActivity)mContext).getSelectedType();
                intent.putExtra("SELECTED_TYPE", selectedType);
                intent.putExtra("ID", leaderboardList[position].id);
                intent.putExtra("CATEGORY", ((SalesRankingActivity)mContext).getSelectedCategory());
                mContext.startActivity(intent);
            }
        });
        viewHolder.rankPosition.setText(String.valueOf(position+1));
    }

    private void setIcon(ImageView imageView, int position) {
        if(SharedUtils.appIsHongKong()&&SharedPreference.getUser(mContext).type.equals(User.USER_TYPE_A)
                &&!(leaderboardList[position].id.equals(SharedPreference.getUser(mContext).id))
                ){
            Glide.with(mContext).load(R.drawable.user_placeholder).placeholder(R.drawable.user_placeholder).dontAnimate().into(imageView);
        }else {
            Glide.with(mContext).load(leaderboardList[position].icon_url).placeholder(R.drawable.user_placeholder).dontAnimate().into(imageView);
        }
    }



    public void cancelTask(){
        if (imageTask != null){
            imageTask.cancel();
        }
    }

    /**
     * Setup layout & data for Shops
     * @param viewHolder
     * @param i
     */
    private void setupShopList(ViewHolder viewHolder, int i) {
        Glide.with(mContext).load(R.drawable.logo).dontAnimate().into(viewHolder.image);
        viewHolder.titleText.setVisibility(View.VISIBLE);
        viewHolder.userNameText.setVisibility(View.GONE);
        viewHolder.userPositionText.setVisibility(View.GONE);
        viewHolder.nextBtn.setVisibility(View.GONE);

        if(ServerPreference.getServerVersion(mContext).equals(ServerPreference.SERVER_VERSION_CN)) {
            viewHolder.titleText.setText(leaderboardList[i].long_description);
        }else {
            viewHolder.titleText.setText(leaderboardList[i].short_description);
        }
        if (leaderboardList[i].percentage != null) {
            Double d = Double.parseDouble(leaderboardList[i].percentage);
            viewHolder.progressBar.setProgress(d.intValue());
        }
        else{
            viewHolder.progressBar.setProgress(0);
        }
        viewHolder.view.setOnClickListener(null);
        viewHolder.rankPosition.setText(String.valueOf(i+1));
    }

    public void setData(LeaderboardData[] l){
        this.leaderboardList = l;
    }

    public void setRankType(String method){
        this.sortBySale  =  method.equals(SalesRankingActivity.SORT_BY_SALE);
    }


    @Override
    public int getItemViewType(int position) {
            return  TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return leaderboardList == null ? 0 : leaderboardList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private View view;
        private ImageView image, nextBtn;
        private TextView rateText, userNameText, userPositionText, targetText, titleText, rankPosition;

        ViewHolder(View view){
            super(view);
            this.view = view;
            progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
            image = (ImageView)view.findViewById(R.id.photo);
            rateText = (TextView)view.findViewById(R.id.target_rate);
            userNameText = (TextView)view.findViewById(R.id.user_name_text);
            userPositionText = (TextView)view.findViewById(R.id.user_position_text);
            targetText = (TextView)view.findViewById(R.id.target_text);
            titleText = (TextView)view.findViewById(R.id.title_text);
            nextBtn = (ImageView)view.findViewById(R.id.next_btn);
            rankPosition = (TextView)view.findViewById(R.id.rank_position);
        }
    }
}
