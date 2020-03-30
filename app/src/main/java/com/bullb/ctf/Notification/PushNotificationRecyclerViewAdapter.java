package com.bullb.ctf.Notification;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Notification;
import com.bullb.ctf.Model.UserNotification;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.WebView.WebViewActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotificationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    public static ArrayList<Notification> dataList;
    public static ArrayList<UserNotification> dataList;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_ITEM = 1;
    private AVLoadingIndicatorView progress;

    public PushNotificationRecyclerViewAdapter(Context context, ArrayList<UserNotification> dataList) {
        this.dataList = dataList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new PushItemViewHolder(mLayoutInflater.inflate(R.layout.notification_item, parent, false));
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PushItemViewHolder) {
            PushItemViewHolder viewHolder = (PushItemViewHolder) holder;
            viewHolder.title.setText(dataList.get(position).title);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    getNotification(dataList.get(position).id);
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("title", dataList.get(position).title);
//                    intent.putExtra("notification_id", dataList.get(position).id);
//                    intent.putExtra("data", dataList.get(position).data.details);
//                    intent.putExtra("data",dataList.get(position).message);
                    intent.putExtra("notification", dataList.get(position).id);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    private void getNotification(final String id){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getNotification(id);
            }
        };
        final KeyTools keyTools = KeyTools.getInstance(mContext);
        ApiService apiService = ServiceGenerator.createService(ApiService.class, mContext);
        final Call<BaseResponse> myNotificationListTask = apiService.getNotificationTask("Bearer " + SharedPreference.getToken(mContext),id);
//        Call<BaseResponse> getNotificationTask = myNotificationListTask;
        myNotificationListTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!myNotificationListTask.isCanceled()) {
                    if (response.isSuccessful()) {
                       String data = keyTools.decryptData(response.body().iv,response.body().data);
                       Log.d("notification", "onResponse: "+data);
                    } else {
                        SharedUtils.handleServerError(mContext, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!myNotificationListTask.isCanceled()){
                    SharedUtils.networkErrorDialogWithRetryUncancellable(mContext, retry);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return  TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    private static class PushItemViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView title;
        private RelativeLayout layout;

        PushItemViewHolder(View view){
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.notification_title);
            layout = (RelativeLayout)view.findViewById(R.id.notification_item_layout);

        }
    }
}