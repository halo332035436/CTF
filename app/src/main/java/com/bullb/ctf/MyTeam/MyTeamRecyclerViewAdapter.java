package com.bullb.ctf.MyTeam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.ManagerRating.ManagerRateActivity;
import com.bullb.ctf.Model.Score;
import com.bullb.ctf.R;
import com.bullb.ctf.SelfManagement.SelfManagementActivity;
import com.bullb.ctf.Utils.ImageCache;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.swipe.SwipeLayout;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTeamRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;
    private static final int MANAGER_RATE_REQUEST = 1;

    private ApiService apiService;
    private KeyTools keyTools;
    public Call<BaseResponse> imageTask;

    final Context mContext;

    private Score[] scores;
    private String department_id;
    private LayoutInflater mLayoutInflater;
    private SearchFilter filter;
    private HashMap<String, String> imageMap = new HashMap<>();
    private LinearLayoutManager layoutManager;

    public MyTeamRecyclerViewAdapter(Context c, Score[] s, String did, LinearLayoutManager layoutManager) {
        this.mContext = c;
        this.scores = s;
        this.department_id = did;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        filter = new SearchFilter(this.scores, this);
        apiService = ServiceGenerator.createService(ApiService.class, c);
        keyTools = KeyTools.getInstance(c);
        this.layoutManager = layoutManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new MyTeamRecyclerViewAdapter.ItemViewHolder(mLayoutInflater.inflate(R.layout.my_team_recyclerview_item, parent, false));
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;

            Glide.with(mContext)
                    .load(scores[i].user.getIconUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.user_placeholder))
                    .into(viewHolder.imageView);
//
//            byte[] image = imageCache.getBitmapFromMemCache(scores[i].user_id);
//            if (image!= null) {
//                Glide.with(mContext).load(image).dontAnimate().into(viewHolder.imageView);
//            }
//            else if (imageMap.containsKey(scores[i].user_id)){
//                Glide.with(mContext).load(R.drawable.circle_grey).dontAnimate().into(viewHolder.imageView);
//                Log.d("debug", "loading or loaded: " + String.valueOf(scores[i].user_id));
//            }else{
//                Glide.with(mContext).load(R.drawable.circle_grey).dontAnimate().into(viewHolder.imageView);
//                getProfileImage(scores[i].user_id, viewHolder.imageView, i);
//            }

            viewHolder.tv_user_name.setText(scores[i].user.name);
            viewHolder.tv_user_position.setText(scores[i].user.title);
            viewHolder.tv_user_id.setText(scores[i].user_id);
            viewHolder.tv_user_department.setText(department_id);
            viewHolder.tv_score.setText(scores[i].getScore());

            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            viewHolder.swipeLayout.setClickToClose(false);
            viewHolder.upperWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.swipeLayout.toggle();
                }
            });

            // Edit this item
            viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MyTeamActivity) mContext).edit_position = i;
                    Intent intent = new Intent(mContext, ManagerRateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SCORE", scores[i]);
                    intent.putExtras(bundle);
                    ((MyTeamActivity) mContext).startActivityForResult(intent, MANAGER_RATE_REQUEST);
                }
            });


        }
    }

//    private void getProfileImage(final String user_id, final ImageView imageView, final int pos){
//        Log.d("debug pos", String.valueOf(pos));
//        imageMap.put(user_id, user_id);
//        imageTask = apiService.getUserProfileTask( "Bearer " + SharedPreference.getToken(mContext), user_id);
//        imageTask.enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                if (!imageTask.isCanceled()) {
//                    if (response.isSuccessful()) {
//                        byte[] imageByte = keyTools.decryptImage(response.body().iv, response.body().data);
////                        imageMap.put(String.valueOf(pos), imageByte);
//                        imageCache.addBitmapToMemoryCache(user_id, imageByte);
//                        if (pos + 2 < layoutManager.findFirstVisibleItemPosition() || pos - 2 > layoutManager.findLastVisibleItemPosition()) {
//                            return;
//                        } else {
//                            Glide.with(mContext).load(imageByte).dontAnimate().into(imageView);
//                        }
//                    } else {
//                        Log.d("debug", "image fail");
//                        imageMap.remove(user_id);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                Log.d("debug", "image fail");
//                if (!imageTask.isCanceled()) {
//                    imageMap.remove(user_id);
//                }
//            }
//        });
//    }

    public void cancelImageTask(){
        if (imageTask != null){
            imageTask.cancel();
        }
    }


    @Override
    public int getItemCount() {
        return scores.length;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    public void setScore(int position, Score score){
        this.scores[position] = score;
    }

    public void setScore(Score[]score){
        this.scores = score;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private ImageView imageView, editBtn;
        private TextView tv_user_name, tv_user_position, tv_user_id, tv_user_department, tv_score;
        private LinearLayout upperWrapper;

        ItemViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.photo);
            tv_user_name = (TextView)view.findViewById(R.id.tv_user_name);
            tv_user_position = (TextView)view.findViewById(R.id.tv_user_position);
            tv_user_id = (TextView)view.findViewById(R.id.tv_user_id);
            tv_user_department = (TextView)view.findViewById(R.id.tv_user_department);
            tv_score = (TextView)view.findViewById(R.id.tv_score);
            swipeLayout = (SwipeLayout)view.findViewById(R.id.swipe_layout);
            editBtn = (ImageView)view.findViewById(R.id.edit_btn);
            upperWrapper = (LinearLayout)view.findViewById(R.id.upper_wrapper);
        }
    }

    public void filterList(String text) {
        filter.filter(text);
    }
}
