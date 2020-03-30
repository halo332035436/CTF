package com.bullb.ctf.TargetManagement.BreakDown;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by oscar on 18/1/16.
 */
public class EditBreakDownViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static ArrayList<String> dataList;
    final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private static final int TYPE_ITEM = 1;
    private UserTargetData userTargetData;
    private SalesData salesData;
    private double managerAdditionA;
    private double managerAdditionF;
    private double managerAdditionE;
    private double managerAdditionM;

    private int precision = 4;

    public EditBreakDownViewAdapter(Context context, ArrayList<String> dataList, UserTargetData userTargetData, SalesData salesData) {
        this.dataList = dataList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.userTargetData = userTargetData;
        this.salesData = salesData;
        managerAdditionA = userTargetData.getManagerAdditionA();
        managerAdditionF = userTargetData.getManagerAdditionF();
        managerAdditionE = userTargetData.getManagerAdditionE();
        managerAdditionM = userTargetData.getManagerAdditionM();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
                return new ItemViewHolder(mLayoutInflater.inflate(R.layout.breakdown_edit_recyclerview_item, parent, false));
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

      if (holder instanceof ItemViewHolder) {
          final ItemViewHolder viewHolder = (ItemViewHolder) holder;

          double progress = 0;

          if (position % 4== 0){
              viewHolder.compareText.setText(mContext.getString(R.string.last_year) + " " + SharedUtils.addCommaToNum(salesData.getLastA()));
//              viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getMinBaseA() + managerAdditionA));
              viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseA() + managerAdditionA));
              viewHolder.typeText.setText(R.string.target_type_A);
              if (userTargetData.getBaseA() == 0){
                  progress = managerAdditionA*100/(userTargetData.getDefaultBaseA()*3);
//                  progress = (managerAdditionA + userTargetData.getDefaultBaseA())*100/(userTargetData.getDefaultBaseA()*3);
              }
              else {
//                  progress = managerAdditionA * 100 / (userTargetData.getBaseA() * 3 - userTargetData.getBaseA());
                  progress = (userTargetData.getDistributedTarget_A())*100/(userTargetData.getBaseA()*3);
              }
//              setMarkPosition(viewHolder.mark, viewHolder.seekBar, 30);
          }
          else if (position%4 ==1){
              viewHolder.compareText.setText(mContext.getString(R.string.last_year) + " " + SharedUtils.addCommaToNum(salesData.getLastF()));
              viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseF() + managerAdditionF));
              viewHolder.typeText.setText(R.string.target_type_F);
              if (userTargetData.getBaseF() == 0){
                  progress = managerAdditionF*100/(userTargetData.getDefaultBaseF()*3);
//                  progress = (managerAdditionF+userTargetData.getDefaultBaseF())*100/(userTargetData.getDefaultBaseF()*3);
              }
              else {
//                  progress = managerAdditionF * 100 / (userTargetData.getBaseF() * 3 - userTargetData.getBaseF());
                  progress = (userTargetData.getDistributedTarget_F())*100/(userTargetData.getBaseF()*3);
              }
//              setMarkPosition(viewHolder.mark, viewHolder.seekBar, 60);
          }
          else if (position%4 == 2){
              viewHolder.compareText.setText(mContext.getString(R.string.last_year) + " " + SharedUtils.addCommaToNum(salesData.getLastE()));
              viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseE() + managerAdditionE));
              viewHolder.typeText.setText(R.string.target_type_E);
              if (userTargetData.getBaseE() == 0){
                  progress = managerAdditionE*100/(userTargetData.getDefaultBaseE()*3);
//                  progress = (managerAdditionE+userTargetData.getDefaultBaseE())*100/(userTargetData.getDefaultBaseE()*3);
              }
              else {
//                  progress = managerAdditionE * 100 / (userTargetData.getBaseE() * 3 - userTargetData.getBaseE());
                  progress = (userTargetData.getDistributedTarget_E())*100/(userTargetData.getBaseE()*3);
              }
//              setMarkPosition(viewHolder.mark, viewHolder.seekBar, 80);
          }else{
              viewHolder.compareText.setText(mContext.getString(R.string.last_year) + " " + SharedUtils.addCommaToNum(salesData.getLastM()));
              viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseM() + managerAdditionM));
              viewHolder.typeText.setText(R.string.target_type_M);
              if (userTargetData.getBaseM() == 0){
                  progress = managerAdditionM*100/(userTargetData.getDefaultBaseM()*3);
//                  progress = (managerAdditionM+userTargetData.getDefaultBaseM())*100/(userTargetData.getDefaultBaseM()*3);
              }
              else {
//                  progress = managerAdditionM * 100 / (userTargetData.getBaseM() * 3 - userTargetData.getBaseM());
                  progress = (userTargetData.getDistributedTarget_M())*100/(userTargetData.getBaseM()*3);
              }
//              setMarkPosition(viewHolder.mark, viewHolder.seekBar, 80);
          }
          viewHolder.seekBar.setProgress((int)SharedUtils.formatDouble(progress));
          viewHolder.seekBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.bar_gradient));
          Drawable dr = mContext.getResources().getDrawable(R.drawable.progress_circle);
          Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
          final Drawable d = new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(bitmap, SharedUtils.dpToPx(26),SharedUtils.dpToPx(26), true));
          viewHolder.seekBar.setThumb(d);

          final SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
              @Override
              public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                  if (position == 0) {
                      managerAdditionA = getAdjustedAmount(userTargetData.getBaseA(), userTargetData.getDefaultBaseA(), i);
//                      viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getMinBaseA() + managerAdditionA));
                      viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseA() + managerAdditionA));
                  } else if (position == 1) {
                      managerAdditionF = getAdjustedAmount(userTargetData.getBaseF(), userTargetData.getDefaultBaseF(), i);
                      viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseF() + managerAdditionF));
                  } else if (position == 2) {
                      managerAdditionE = getAdjustedAmount(userTargetData.getBaseE(),  userTargetData.getDefaultBaseE(), i);
                      viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseE() + managerAdditionE));
                  } else if (position == 3) {
                      managerAdditionM = getAdjustedAmount(userTargetData.getBaseM(),  userTargetData.getDefaultBaseM(), i);
                      viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getBaseM() + managerAdditionM));
                  }
                  ((EditBreakDownActivity) mContext).notifyDataSetChanged(SharedUtils.round(managerAdditionA,2), SharedUtils.round(managerAdditionE,2), SharedUtils.round(managerAdditionF,2), SharedUtils.round(managerAdditionM,2));
              }

              @Override
              public void onStartTrackingTouch(SeekBar seekBar) {
              }

              @Override
              public void onStopTrackingTouch(SeekBar seekBar) {
              }
          };

          viewHolder.seekBar.setOnSeekBarChangeListener(listener);

          viewHolder.salesText.addTextChangedListener(new TextWatcher() {
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


//                  if(target < 1) {
//                      viewHolder.salesText.setText("1");
//                      return;
//                  }
                  if (position == 0) {
                      if(target > SharedUtils.round(userTargetData.getMinBaseA()*3,0)){
                          viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getMinBaseA()*3));
                          return;
                      }

//                      managerAdditionA = SharedUtils.round((target - userTargetData.getMinBaseA()),precision);
//                          viewHolder.seekBar.setProgress(getTargetPercentage(userTargetData.getMinBaseA(),
//                                  target - userTargetData.getMinBaseA()));
                    BigDecimal targetD = BigDecimal.valueOf(target);
                    double difference = targetD.subtract(BigDecimal.valueOf(userTargetData.getBaseA())).doubleValue();
                    managerAdditionA = difference;
//                      managerAdditionA = SharedUtils.round((target - userTargetData.getBaseA()), precision);
                      viewHolder.seekBar.setProgress(getTargetPercentageWithBaseMin(userTargetData.getBaseA(),
                              difference, userTargetData.getDefaultBaseA()));


                  } else if (position == 1) {
                          if(target > SharedUtils.round(userTargetData.getMinBaseF()*3,0)){
                              viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getMinBaseF()*3));
                              return;
                          }
                      BigDecimal targetD = BigDecimal.valueOf(target);
                      double difference = targetD.subtract(BigDecimal.valueOf(userTargetData.getBaseF())).doubleValue();
                      managerAdditionF = difference;

//                          managerAdditionF = SharedUtils.round((target - userTargetData.getBaseF()),precision);
                          viewHolder.seekBar.setProgress(getTargetPercentageWithBaseMin(userTargetData.getBaseF(),
                                  difference, userTargetData.getDefaultBaseF()));
                  } else if (position == 2) {     if(target > SharedUtils.round(userTargetData.getMinBaseE()*3,0)){
                              viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getMinBaseE()*3));
                              return;
                          }
                      BigDecimal targetD = BigDecimal.valueOf(target);
                      double difference = targetD.subtract(BigDecimal.valueOf(userTargetData.getBaseE())).doubleValue();
                      managerAdditionE = difference;

//                          managerAdditionE = SharedUtils.round((target - userTargetData.getBaseE()),precision);
                          viewHolder.seekBar.setProgress(getTargetPercentageWithBaseMin(userTargetData.getBaseE(),
                                  difference, userTargetData.getDefaultBaseE()));
                  } else if (position == 3) {
                          if(target > SharedUtils.round(userTargetData.getMinBaseM()*3,0)){
                              viewHolder.salesText.setText(SharedUtils.addCommaToNum(userTargetData.getMinBaseM()*3));
                              return;
                          }

                      BigDecimal targetD = BigDecimal.valueOf(target);
                      double difference = targetD.subtract(BigDecimal.valueOf(userTargetData.getBaseM())).doubleValue();
                      managerAdditionM = difference;
//                          managerAdditionM = SharedUtils.round((target - userTargetData.getBaseM()),precision);
                          viewHolder.seekBar.setProgress(getTargetPercentageWithBaseMin(userTargetData.getBaseM(),
                                  difference, userTargetData.getDefaultBaseM()));
                  }
                  viewHolder.seekBar.refreshDrawableState();
                  viewHolder.seekBar.setOnSeekBarChangeListener(listener);
                  ((EditBreakDownActivity) mContext).notifyDataSetChanged(SharedUtils.round(managerAdditionA, 2), SharedUtils.round(managerAdditionE, 2), SharedUtils.round(managerAdditionF, 2), SharedUtils.round(managerAdditionM, 2));
              }
          });
      }
    }

    private int getTargetPercentageWithBaseMin(double distributedTarget, double userAddition, double baseTarget){
        if(distributedTarget<=0){
            return (int)(SharedUtils.formatDouble(userAddition / (baseTarget*3))*100);

        }else{
            return getTargetPercentage(distributedTarget, userAddition);
        }
        }

    private int getTargetPercentage(double distributedTarget, double userAddition){
        return (int)(SharedUtils.formatDouble((userAddition + distributedTarget) / (distributedTarget*3))*100);
    }

    private double getAdjustedAmountWithBaseMin(double baseTarget, double defaultBaseTarget, int percentage){
        //handle case of amount 0
        if (baseTarget == 0){
            return defaultBaseTarget *3 * percentage/100;
        }
        // normal case
        return ((baseTarget*3) -baseTarget) *percentage /100;
    }
    
    private double getAdjustedAmount(double baseTarget, double defaultBaseTarget, int percentage){
        BigDecimal bigBase = BigDecimal.valueOf(baseTarget);
        BigDecimal bigDefault = BigDecimal.valueOf(defaultBaseTarget);
        BigDecimal bigPercent = BigDecimal.valueOf(percentage);
        if(baseTarget == 0){
            Log.d("baseTarget", "percentage: "+percentage+" return: "+SharedUtils.round((defaultBaseTarget * 3 * percentage / 100 - defaultBaseTarget), precision));

            double result = bigDefault.multiply(BigDecimal.valueOf(3)).multiply(bigPercent).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).doubleValue();

//            return SharedUtils.round((defaultBaseTarget * 3 * percentage / 100), precision);
            return SharedUtils.round(result, precision);
        }else {

            double result = bigDefault.multiply(BigDecimal.valueOf(3)).multiply(bigPercent).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).min(bigBase).doubleValue();

//            return SharedUtils.round((baseTarget * 3 * percentage / 100 - baseTarget), precision);
            return SharedUtils.round(result, precision);
        }
    }


    private void setMarkPosition(final View markView,final SeekBar seekBar ,final int percentage){
        seekBar.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)markView.getLayoutParams();
                Log.d("width",String.valueOf(seekBar.getWidth()));
                int seekBarThumbWidth = (int) mContext.getResources().getDimension(R.dimen.seek_bar_thumb_size);
                int marginRelativeToBar = (seekBar.getWidth() - seekBarThumbWidth ) * percentage/100;
                int barSizeWithMargin = (seekBar.getWidth() - seekBarThumbWidth - SharedUtils.dpToPx(2));
                params.setMargins(seekBarThumbWidth/2 + Math.min(marginRelativeToBar, barSizeWithMargin), 0,0 , 0); //substitute parameters for left, top, right, bottom
                markView.setLayoutParams(params);
            }
        });
    }

    public double getManagerAdditionA() {
        return SharedUtils.round(managerAdditionA,4);
    }

    public double getManagerAdditionF() {
        return SharedUtils.round(managerAdditionF,4);
    }

    public double getManagerAdditionE() {
        return SharedUtils.round(managerAdditionE,4);
    }

    public double getManagerAdditionM() {
        return SharedUtils.round(managerAdditionM,4); }

    @Override
    public int getItemViewType(int position) {
            return  TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private SeekBar seekBar;
        private TextView compareText, typeText;
        private EditText salesText;
        private View mark;

        ItemViewHolder(View view){
            super(view);
            seekBar = (SeekBar)view.findViewById(R.id.seek_bar);
            typeText = (TextView)view.findViewById(R.id.type_text);
            compareText = (TextView)view.findViewById(R.id.compare_text);
            salesText = (EditText)view.findViewById(R.id.sales_target_text);
//            mark = view.findViewById(R.id.mark);

        }
    }


}
