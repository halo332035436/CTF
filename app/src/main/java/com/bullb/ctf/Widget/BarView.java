package com.bullb.ctf.Widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;

import java.text.DecimalFormat;

/**
 * Created by oscarlaw on 6/10/2016.
 */

public class BarView extends View {
    private final Paint paint;

    private double heightPercentage;
    private int barHeight;
    private int barWidth;
    private int barTextSize;
    private double num;


    public BarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        num =0;
        barHeight = (int) getResources().getDimension(R.dimen.bar_height);
        barWidth = (int) getResources().getDimension(R.dimen.bar_width);
        barTextSize = (int) getResources().getDimension(R.dimen.bar_text);
        heightPercentage = 0;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        int[] colors2 = {ContextCompat.getColor(context,R.color.orange),ContextCompat.getColor(context,R.color.test_red)};
        float[] positions2 = {0,1f};
        LinearGradient gradient = new LinearGradient(0,SharedUtils.dpToPx(10),barWidth, barHeight,colors2,positions2, Shader.TileMode.MIRROR);
        paint.setShader(gradient);
        //Initial Angle (optional, it can be zero)
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint mypaint =new Paint();
        mypaint.setAntiAlias(true);
        String yourFormattedString = SharedUtils.addCommaToNum(num);
        mypaint.setTextSize(barTextSize);//設定字體大小
        mypaint.setColor(ContextCompat.getColor(getContext(),R.color.main_red));//設定字體顏色
        float textWidth = mypaint.measureText(yourFormattedString);
        int barMaxHeight = barHeight - barTextSize;

        if (textWidth >0) {
            canvas.drawText(yourFormattedString, getWidth()/2 - textWidth / 2, barTextSize + (barMaxHeight * (1-(float)heightPercentage)) , mypaint);
        }
        else{
            canvas.drawText(yourFormattedString, 0, SharedUtils.dpToPx(10), mypaint);
        }
        canvas.drawRect(getWidth()/2-barWidth/2,  barTextSize + SharedUtils.dpToPx(2)+ (barMaxHeight * (1-(float)heightPercentage)),getWidth()/2+barWidth/2, getHeight(), paint);
    }

    public double getHeightPercentage() {
        return heightPercentage;
    }

    public void setHeightPercentage(float heightPercentage) {
        this.heightPercentage = heightPercentage;
        this.requestLayout();
    }

    public double getBarNum() {
        return num;
    }

    public void setBarNum(int num) {
        this.num = num;
        this.requestLayout();
    }

    public void setHeightPercentage(double heightPercentage,double num) {
        this.heightPercentage = heightPercentage;
        this.num = num;
        this.requestLayout();
    }




}
