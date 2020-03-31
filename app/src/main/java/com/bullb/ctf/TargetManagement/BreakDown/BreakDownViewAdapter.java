package com.bullb.ctf.TargetManagement.BreakDown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.DepartmentTargetData;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserTarget;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.SelfManagement.SelfManagementActivity;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.Date;
import com.bullb.ctf.Utils.ExtendedEditText;
import com.bullb.ctf.Utils.ImageCache;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.CircleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreakDownViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<User> dataList;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private CircleView circleView;
    private User user;
    private DepartmentTargetData departmentTargetData;
    private ApiService apiService;
    private KeyTools keyTools;
    private HashMap<String, String> imageMap = new HashMap<>();
    private LinearLayoutManager layoutManager;
    private Call<BaseResponse> imageTask;
    private String fromDate;

    private Date currentDay;

    public BreakDownViewAdapter(Context context, ArrayList<User> dataList, LinearLayoutManager layoutManager) {
        this.dataList = dataList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        user = SharedPreference.getUser(context);
        apiService = ServiceGenerator.createService(ApiService.class, context);
        keyTools = KeyTools.getInstance(context);
        this.layoutManager = layoutManager;
        this.currentDay = new Date();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.breakdown_recyclerview_header, parent, false));

        }
        else if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            if(SharedUtils.serverIsHongKong(mContext)) {
                return new ItemViewHolder(mLayoutInflater.inflate(R.layout.breakdown_recyclerview_item, parent, false));
            }else{
                return new ItemViewHolder(mLayoutInflater.inflate(R.layout.breakdown_recyclerview_cn_item, parent, false));
            }
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder){
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            circleView = headerHolder.circleView;
            setHeaderData(headerHolder);
        }
        else if (holder instanceof ItemViewHolder) {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            final User user = (User)dataList.get(position-1);
            Log.d("TargetTest", "adapter: " + user.name);
//
            for(UserTarget target: user.user_targets) {
                Log.d("TargetTest", "adapter: " + target.type + " -> "+target.manager_addition);
            }


            final UserTargetData userTargetData = new UserTargetData(user.user_targets,fromDate);
            SalesData salesData = new SalesData(user.user_sales, fromDate);

//            Log.d("TargetTest", "adapter: " + userTargetData.getManagerAdditionA() );

            //load User Image
            if(SharedUtils.serverIsHongKong(mContext)) {
                viewHolder.progressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.bar_gradient));
                Glide.with(mContext)
                        .load(user.getIconUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.user_placeholder))
                        .into(viewHolder.imageView);
                viewHolder.limitText.setText(mContext.getString(R.string.lower_limit) + " " + SharedUtils.addCommaToNum(userTargetData.getBaseAll()));
                viewHolder.progressBar.setProgress((int)SharedUtils.formatDouble(userTargetData.getBaseOverDistributedRatio()));

            }else{
                setupEditTextHandler(viewHolder, user);

                viewHolder.originalTargetText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseAll()));
                viewHolder.originalTargetA.setText(SharedUtils.addCommaToNum(userTargetData.getBaseA()));
                viewHolder.originalTargetE.setText(SharedUtils.addCommaToNum(userTargetData.getBaseE()));
                viewHolder.originalTargetF.setText(SharedUtils.addCommaToNum(userTargetData.getBaseF()));

                viewHolder.targetA.setTag("program change");
                viewHolder.targetE.setTag("program change");
                viewHolder.targetF.setTag("program change");
                viewHolder.targetA.setText(Long.toString(Math.round(userTargetData.getDistributedTarget_A())));
                viewHolder.targetE.setText(Long.toString(Math.round(userTargetData.getDistributedTarget_E())));
                viewHolder.targetF.setText(Long.toString(Math.round(userTargetData.getDistributedTarget_F())));

                viewHolder.targetA.setTag(null);
                viewHolder.targetE.setTag(null);
                viewHolder.targetF.setTag(null);

              }
//            loadUserImage(user.id,viewHolder.imageView, position);

            viewHolder.userNameText.setText(user.name);
            viewHolder.userPositionText.setText(user.title);
            viewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));
            viewHolder.lastYearSalesText.setText(SharedUtils.addCommaToNum(salesData.getLastTotalSales()));

            //            viewHolder.progressBar.setProgress((int)SharedUtils.formatDouble(userTargetData.getDistributedOverMaxRatio()));

            viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            viewHolder.swipeLayout.setClickToClose(false);
            viewHolder.upperLayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.swipeLayout.toggle();
                }
            });

//add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
            viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.view.findViewById(R.id.bottom_wrapper));

            viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserTarget targetM = null;
                    // check user has next month target or not
                    UserTarget targetA = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_A, getStartDate(Calendar.getInstance()));
                    UserTarget targetF = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_F, getStartDate(Calendar.getInstance()));
                    UserTarget targetE = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_E, getStartDate(Calendar.getInstance()));
                    if(ServerPreference.getServerVersion(mContext).equals(ServerPreference.SERVER_VERSION_HK)) {
                        targetM = userTargetData.getCurrentMonthTarget(UserTarget.TYPE_M, getStartDate(Calendar.getInstance()));
                    }
//                    if(ServerPreference.getServerVersion(mContext).equals(ServerPreference.SERVER_VERSION_HK)&&
//                            currentDay.getDay()>7){
//                        Toast.makeText(mContext, R.string.error_target_edit_expired, Toast.LENGTH_SHORT).show();
//                    } else
                    if (targetA == null || targetF == null || targetE == null || (
                            ServerPreference.getServerVersion(mContext).equals(ServerPreference.SERVER_VERSION_HK) && targetM == null
                    )){
                        if(ServerPreference.getServerVersion(mContext).equals(ServerPreference.SERVER_VERSION_CN)) {
                            Toast.makeText(mContext, R.string.error_no_next_target, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, R.string.error_no_current_target, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Intent intent = new Intent(mContext, EditBreakDownActivity.class);
                        intent.putExtra("user", new Gson().toJson(user));
                        ((Activity) mContext).startActivityForResult(intent, 111);
                        viewHolder.swipeLayout.close();
                    }
                }
            });

        }
    }


    private void setupEditTextHandler(final ItemViewHolder viewHolder, final User user){
        final ItemViewHolder mViewHolder = viewHolder;

        TextWatcher watcherA = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("TextChangeBug", "afterTextChanged: targetA tag"+viewHolder.targetA.getTag());

                if(viewHolder.targetA.getTag() != null){
                    return;
                }




                String text;
                if(TextUtils.isEmpty(editable)){
                    return;
                }
                if(editable.toString().contains(",")){
                    text = editable.toString().replace(",","");
                }else{
                    text = editable.toString();
                }
                double target = Double.valueOf(text);
//                if(target < 1){
//                    viewHolder.targetA.setText("1");
//                    return;
//                }
//                target = 1;

                UserTargetData userTargetData = new UserTargetData(user.user_targets, getStartDate(Calendar.getInstance()));
//                mViewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));
                if(target > SharedUtils.round(userTargetData.getMinBaseA()*3,0)){
                    mViewHolder.targetA.setText(String.valueOf((int)SharedUtils.round(userTargetData.getMinBaseA()*3,0)));
                    target = Double.valueOf(mViewHolder.targetA.getText().toString());
//                    return;
                }
                BigDecimal targetD = BigDecimal.valueOf(target);
                user.getTargetByType(UserTarget.TYPE_A).manager_addition = targetD.subtract(BigDecimal.valueOf(user.getTargetByType(UserTarget.TYPE_A).base_amount)).doubleValue();
//                user.getTargetByType(UserTarget.TYPE_A).manager_addition = SharedUtils.round(target - user.getTargetByType(UserTarget.TYPE_A).base_amount,4);
                userTargetData = new UserTargetData(user.user_targets, getStartDate(Calendar.getInstance()));
                mViewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));

                Log.d("TextChangeBug", "afterTextChanged: "+user.name+"'s A -> "+user.getTargetByType(UserTarget.TYPE_A).manager_addition);

//                notifyDataSetChanged();
            }
        };

        mViewHolder.targetA.clearTextChangedListeners();

        if(user.getTargetByType(UserTarget.TYPE_A) ==null) {
            mViewHolder.targetA.setEnabled(false);
        }else{
            mViewHolder.targetA.setEnabled(true);
        }
        mViewHolder.targetA.addTextChangedListener(watcherA);

        TextWatcher watcherE = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("TextChangeBug", "afterTextChanged: targetE tag"+viewHolder.targetE.getTag());
                if(viewHolder.targetE.getTag() != null){
                    return;
                }
                String text;
                if(TextUtils.isEmpty(editable)){
                    return;
                }
                if(editable.toString().contains(",")){
                    text = editable.toString().replace(",","");
                }else{
                    text = editable.toString();
                }
                double target = Double.valueOf(text);
//                if(target < 1){
//                    viewHolder.targetE.setText("1");
//                    return;
//                }
//                target = 1;
                UserTargetData userTargetData = new UserTargetData(user.user_targets, getStartDate(Calendar.getInstance()));
//                mViewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));
                if(target > SharedUtils.round(userTargetData.getMinBaseE()*3,0)){
                    mViewHolder.targetE.setText(String.valueOf((int)SharedUtils.round(userTargetData.getMinBaseE()*3,0)));
                    target = Double.valueOf(mViewHolder.targetE.getText().toString());
//                    return;
                }
                BigDecimal targetD = BigDecimal.valueOf(target);
                user.getTargetByType(UserTarget.TYPE_E).manager_addition = targetD.subtract(BigDecimal.valueOf(user.getTargetByType(UserTarget.TYPE_E).base_amount)).doubleValue();
//                user.getTargetByType(UserTarget.TYPE_E).manager_addition = SharedUtils.round(target - user.getTargetByType(UserTarget.TYPE_E).base_amount,4);
                userTargetData = new UserTargetData(user.user_targets, getStartDate(Calendar.getInstance()));
                mViewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));

                Log.d("TextChangeBug", "afterTextChanged: "+user.name+"'s E -> "+user.getTargetByType(UserTarget.TYPE_E).manager_addition);

//                notifyDataSetChanged();
            }
        };

        mViewHolder.targetE.clearTextChangedListeners();
        if(user.getTargetByType(UserTarget.TYPE_E) ==null) {
            mViewHolder.targetE.setEnabled(false);
        }else{
            mViewHolder.targetE.setEnabled(true);
        }
        mViewHolder.targetE.addTextChangedListener(watcherE);

        TextWatcher watcherF = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("TextChangeBug", "afterTextChanged: targetF tag"+viewHolder.targetF.getTag());
                if(viewHolder.targetF.getTag() != null){
                    return;
                }
                String text;
                if(TextUtils.isEmpty(editable)){
                    return;
                }
                if(editable.toString().contains(",")){
                    text = editable.toString().replace(",","");
                }else{
                    text = editable.toString();
                }
                double target = Double.valueOf(text);
//                if(target < 1){
//                    viewHolder.targetF.setText("1");
//                    return;
//                }
//                target = 1;
                UserTargetData userTargetData = new UserTargetData(user.user_targets, getStartDate(Calendar.getInstance()));
//                mViewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));
                if(target > SharedUtils.round(userTargetData.getMinBaseF()*3,0)){
                    mViewHolder.targetF.setText(String.valueOf((int)SharedUtils.round(userTargetData.getMinBaseF()*3,0)));
                    target = Double.valueOf(mViewHolder.targetF.getText().toString());
//                    return;
                }
                BigDecimal targetD = BigDecimal.valueOf(target);
                user.getTargetByType(UserTarget.TYPE_F).manager_addition = targetD.subtract(BigDecimal.valueOf(user.getTargetByType(UserTarget.TYPE_F).base_amount)).doubleValue();
//                user.getTargetByType(UserTarget.TYPE_F).manager_addition = SharedUtils.round(target - user.getTargetByType(UserTarget.TYPE_F).base_amount,4);
                userTargetData = new UserTargetData(user.user_targets, getStartDate(Calendar.getInstance()));
                mViewHolder.targetText.setText(SharedUtils.addCommaToNum(userTargetData.getDistributedTarget()));

                Log.d("TextChangeBug", "afterTextChanged: "+user.name+"'s F -> "+user.getTargetByType(UserTarget.TYPE_F).manager_addition);

//                notifyDataSetChanged();
            }
        };

        mViewHolder.targetF.clearTextChangedListeners();
        if(user.getTargetByType(UserTarget.TYPE_F) ==null) {
            mViewHolder.targetF.setEnabled(false);
        }else{
            mViewHolder.targetF.setEnabled(true);
        }
        mViewHolder.targetF.addTextChangedListener(watcherF);
    }

    private String getStartDate(Calendar cal) {
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH)+1);

        if (month.length() < 2) {
            month = "0" + month;
        }
        return year + "-" + month + "-01";
    }


//    private void loadUserImage(String userId , ImageView imageView, int position){
//        byte[] image = imageCache.getBitmapFromMemCache(userId);
//        if (image!= null) {
//            Glide.with(mContext).load(image).dontAnimate().into(imageView);
//        }
//        else if (imageMap.containsKey(userId)){
//            Glide.with(mContext).load(R.drawable.circle_grey).dontAnimate().into(imageView);
//            Log.d("debug", "loading or loaded: " + String.valueOf(position));
//        }else{
//            Glide.with(mContext).load(R.drawable.circle_grey).dontAnimate().into(imageView);
//            getProfileImage(userId, imageView, position);
//        }
//    }


//    private void getProfileImage(final String user_id, final ImageView imageView, final int pos){
//        Log.d("debug pos", String.valueOf(pos));
//        imageMap.put(user_id, user_id);
//        imageTask = apiService.getUserProfileTask( "Bearer " + SharedPreference.getToken(mContext), user_id);
//        imageTask.enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                if (response.isSuccessful()) {
//                    byte[] imageByte = keyTools.decryptImage(response.body().iv, response.body().data);
////                        imageMap.put(String.valueOf(pos), imageByte);
//                    imageCache.addBitmapToMemoryCache(user_id, imageByte);
//                    if (pos+2 < layoutManager.findFirstVisibleItemPosition() || pos-2> layoutManager.findLastVisibleItemPosition()){
//                        return;
//                    }
//                    else{
//                        Glide.with(mContext).load(imageByte).dontAnimate().into(imageView);
//                    }
//                }
//                else{
//                    Log.d("debug", "image fail");
//                    imageMap.remove(user_id);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                Log.d("debug", "image fail");
//                imageMap.remove(user_id);
//            }
//        });
//    }

    public void cancelImageTask(){
        if (imageTask != null)
            imageTask.cancel();
    }


    private void setHeaderData(HeaderViewHolder headerHolder) {
        String baseTarget = "";
        String decomposedTarget = "";
        if (departmentTargetData != null) {
            baseTarget = SharedUtils.addCommaToNum(departmentTargetData.getAmountAll());
            decomposedTarget = SharedUtils.addCommaToNum(departmentTargetData.getDecomposedTargetAll());
        }
        CircleData data1 = new CircleData(mContext.getString(R.string.branches_total_indicator),baseTarget,CircleData.TYPE_MONEY);
        CircleData data2 = new CircleData(mContext.getString(R.string.breakdown_sales_target), decomposedTarget,CircleData.TYPE_MONEY);
        headerHolder.circleView.setData(data1,data2);
    }


    public void setData(DepartmentTargetData departmentTargetData, String fromDate){
        this.departmentTargetData = departmentTargetData;
        this.fromDate =  fromDate;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_HEADER;
        }
        else {
            return  TYPE_ITEM;
        }
    }



    @Override
    public int getItemCount() {
        return dataList == null ? 1 : dataList.size()+1;
    }

    public void scrollImage(int scrollY){
        ViewHelper.setTranslationY(circleView, (float)(scrollY/3));
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private View view;
        private ImageView imageView, editBtn;
        private TextView limitText, userNameText,userPositionText, targetText, lastYearSalesText;
        private SwipeLayout swipeLayout;
        private LinearLayout upperLayer;

        private TextView originalTargetText, originalTargetA, originalTargetE, originalTargetF;
        private ExtendedEditText targetA, targetE, targetF;


        ItemViewHolder(View view){
            super(view);
            this.view = view;
            progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
            imageView = (ImageView)view.findViewById(R.id.photo);
            limitText = (TextView)view.findViewById(R.id.lower_limit_text);
            userNameText = (TextView)view.findViewById(R.id.user_name_text);
            userPositionText = (TextView)view.findViewById(R.id.user_position_text);
            targetText = (TextView)view.findViewById(R.id.target_text);
            lastYearSalesText = (TextView)view.findViewById(R.id.last_year_sales_text);
            swipeLayout = (SwipeLayout)view.findViewById(R.id.swipe_layout);
            editBtn = (ImageView)view.findViewById(R.id.edit_btn);
            upperLayer = (LinearLayout)view.findViewById(R.id.upper_layer);

            originalTargetText = (TextView)view.findViewById(R.id.original_target_text);
            originalTargetA = (TextView)view.findViewById(R.id.original_target_a);
            originalTargetE = (TextView)view.findViewById(R.id.original_target_e);
            originalTargetF = (TextView)view.findViewById(R.id.original_target_f);

            targetA = (ExtendedEditText) view.findViewById(R.id.target_a);
            targetE = (ExtendedEditText) view.findViewById(R.id.target_e);
            targetF = (ExtendedEditText) view.findViewById(R.id.target_f);

        }
    }



    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private CircleView circleView;

        HeaderViewHolder(View view){
            super(view);
            circleView = (CircleView)view.findViewById(R.id.circle_view);
        }
    }



}
