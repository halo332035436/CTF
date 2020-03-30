package com.bullb.ctf.TargetManagement.Predict;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;


import java.util.ArrayList;


/**
 * Created by oscar on 18/1/16.
 */
public class PredictCommissionViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SeekBar.OnSeekBarChangeListener{

    public static ArrayList<String> dataList;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_ITEM = 1;
    private UserTargetData targetData;
    
    private boolean targetTextEdit = false;


    private double adjustA, adjustF, adjustE, adjustM;


    public PredictCommissionViewAdapter(Context context, ArrayList<String> dataList, UserTargetData targetData) {
        this.dataList = dataList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.targetData = targetData;
        adjustA = targetData.getUserAdditionA();
        adjustF = targetData.getUserAdditionF();
        adjustE = targetData.getUserAdditionE();
        adjustM = targetData.getUserAdditionM();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new ItemViewHolder(mLayoutInflater.inflate(R.layout.predict_commission_recyclerview_item, parent, false));
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.seekBar.setOnSeekBarChangeListener(null);
            viewHolder.seekBar.setMax(10000);//precision
            double progress = 0;
            if (position == 0) {
                viewHolder.seekBar.setProgress(getProgressPercentage(targetData.getDistributedTarget_A(), adjustA));
                viewHolder.typeText.setText(R.string.target_type_A);
                viewHolder.lastYearText.setText(mContext.getString(R.string.predict_distributed_target) + "\n" + SharedUtils.addCommaToNum(targetData.getDistributedTarget_A()));
                viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_A() + adjustA));
                progress = adjustA*100/(targetData.getDistributedTarget_A()*3 - targetData.getDistributedTarget_A());
            } else if (position == 1) {
                viewHolder.seekBar.setProgress(getProgressPercentage(targetData.getDistributedTarget_F(), adjustF));
                viewHolder.typeText.setText(R.string.target_type_F);
                viewHolder.lastYearText.setText(mContext.getString(R.string.predict_distributed_target) + "\n" + SharedUtils.addCommaToNum(targetData.getDistributedTarget_F()));
                viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_F() + adjustF));
                progress = adjustF*100/(targetData.getDistributedTarget_F()*3 - targetData.getDistributedTarget_F());

            } else if (position == 2) {
                viewHolder.seekBar.setProgress(getProgressPercentage(targetData.getDistributedTarget_E(), adjustE));
                viewHolder.typeText.setText(R.string.target_type_E);
                viewHolder.lastYearText.setText(mContext.getString(R.string.predict_distributed_target) + "\n" + SharedUtils.addCommaToNum(targetData.getDistributedTarget_E()));
                viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_E() + adjustE));
                progress = adjustE*100/(targetData.getDistributedTarget_E()*3 - targetData.getDistributedTarget_E());
            } else if (position == 3) {
                viewHolder.seekBar.setProgress(getProgressPercentage(targetData.getDistributedTarget_M(), adjustM));
                viewHolder.typeText.setText(R.string.target_type_M);
                viewHolder.lastYearText.setText(mContext.getString(R.string.predict_distributed_target) + "\n" + SharedUtils.addCommaToNum(targetData.getDistributedTarget_M()));
                viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_M() + adjustM));
                progress = adjustM*100/(targetData.getDistributedTarget_M()*3 - targetData.getDistributedTarget_M());
            }

            viewHolder.seekBar.setProgress((int)SharedUtils.formatDouble(progress));
            viewHolder.seekBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.bar_gradient));
            Drawable dr = mContext.getResources().getDrawable(R.drawable.progress_circle);
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
            Drawable d = new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(bitmap, SharedUtils.dpToPx(26),SharedUtils.dpToPx(26), true));
            viewHolder.seekBar.setThumb(d);

            final SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if(!targetTextEdit) {
                        double percentage = i / 100d;
                        if (position == 0) {
                            adjustA = getAdjustedAmount(targetData.getDistributedTarget_A(), percentage);
                            viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_A() + adjustA));
                        } else if (position == 1) {
                            adjustF = getAdjustedAmount(targetData.getDistributedTarget_F(), percentage);
                            viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_F() + adjustF));
                        } else if (position == 2) {
                            adjustE = getAdjustedAmount(targetData.getDistributedTarget_E(), percentage);
                            viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_E() + adjustE));
                        } else if (position == 3) {
                            adjustM = getAdjustedAmount(targetData.getDistributedTarget_M(), percentage);
                            viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_M() + adjustM));
                        }
                        ((PredictCommissionActivity) mContext).notifyDataSetChanged(SharedUtils.round(adjustA, 2), SharedUtils.round(adjustE, 2), SharedUtils.round(adjustF, 2), SharedUtils.round(adjustM, 2));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    ((PredictCommissionActivity) mContext).hideKeyboard();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

            viewHolder.seekBar.setOnSeekBarChangeListener(listener);

            viewHolder.targetText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String text;
                    if(TextUtils.isEmpty(editable)){
                        return;
                    }
                    if(editable.toString().contains(",")){
                        text = editable.toString().replace(",","");
                    }else{
                        text = editable.toString();
                    }
                    viewHolder.seekBar.setOnSeekBarChangeListener(null);
                    double target = Double.valueOf(text);
                    if (position == 0) {
                        if(target >= targetData.getDistributedTarget_A()){

                            if(target > SharedUtils.round(targetData.getDistributedTarget_A()*3,0)){
                                viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_A()*3));
                                return;
                            }

                            adjustA = target - targetData.getDistributedTarget_A();
                            viewHolder.seekBar.setProgress(getTargetPercentage(targetData.getDistributedTarget_A(),
                                    target- targetData.getDistributedTarget_A()));
                        }
                    } else if (position == 1) {
                        if(target >= targetData.getDistributedTarget_F()){

                            if(target > SharedUtils.round(targetData.getDistributedTarget_F()*3,0)){
                                viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_F()*3));
                                return;
                            }

                            adjustF = target - targetData.getDistributedTarget_F();
                            viewHolder.seekBar.setProgress(getTargetPercentage(targetData.getDistributedTarget_F(),
                                    target - targetData.getDistributedTarget_F()));
                        }
                    } else if (position == 2) {
                        if(target >= targetData.getDistributedTarget_E()){

                            if(target > SharedUtils.round(targetData.getDistributedTarget_E()*3,0)){
                                viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_E()*3));
                                return;
                            }

                            adjustE = target - targetData.getDistributedTarget_E();
                            viewHolder.seekBar.setProgress(getTargetPercentage(targetData.getDistributedTarget_E(),
                                    target - targetData.getDistributedTarget_E()));
                        }
                    } else if (position == 3) {
                        if(target >= targetData.getDistributedTarget_M()){

                            if(target > SharedUtils.round(targetData.getDistributedTarget_M()*3,0)){
                                viewHolder.targetText.setText(SharedUtils.addCommaToNum(targetData.getDistributedTarget_M()*3));
                                return;
                            }

                            adjustM = target - targetData.getDistributedTarget_M();
                            viewHolder.seekBar.setProgress(getTargetPercentage(targetData.getDistributedTarget_M(),
                                    target - targetData.getDistributedTarget_M()));
                        }
                    }
                    viewHolder.seekBar.refreshDrawableState();
                    viewHolder.seekBar.setOnSeekBarChangeListener(listener);
                    ((PredictCommissionActivity) mContext).notifyDataSetChanged(SharedUtils.round(adjustA, 2), SharedUtils.round(adjustE, 2), SharedUtils.round(adjustF, 2), SharedUtils.round(adjustM, 2));
                }
            });
        }
    }

    public double getAdjustA() {
        return SharedUtils.round(adjustA,2);
    }

    public double getAdjustF() {
        return SharedUtils.round(adjustF,2);
    }

    public double getAdjustE() {
        return SharedUtils.round(adjustE,2);
    }

    public double getAdjustM() {
        return SharedUtils.round(adjustM,2);
    }

    public void setAdjusted(double adjustA, double adjustF, double adjustE, double adjustM){
        this.adjustA = adjustA;
        this.adjustF = adjustF;
        this.adjustE = adjustE;
        this.adjustM = adjustM;
        notifyDataSetChanged();

    }

    private double getAdjustedAmount(double distributedTarget, double percentage){
        return ((distributedTarget*3) -distributedTarget) *percentage /100;
    }

    private int getProgressPercentage(double distributedTarget, double userAddition ){
        return (int)SharedUtils.formatDouble(userAddition / (distributedTarget*3));
    }

    private int getTargetPercentage(double distributedTarget, double userAddition){
        return (int)(SharedUtils.formatDouble(userAddition / (distributedTarget*2))*100)*100;
    }

    @Override
    public int getItemViewType(int position) {
        return  TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private SeekBar seekBar;
        private TextView typeText, lastYearText;
        private EditText targetText;


        ItemViewHolder(View view){
            super(view);
            seekBar = (SeekBar)view.findViewById(R.id.seek_bar);
            typeText = (TextView)view.findViewById(R.id.type_text);
            targetText = (EditText)view.findViewById(R.id.target_text);
            lastYearText = (TextView)view.findViewById(R.id.last_year_text);

        }
    }
}
