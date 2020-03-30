package com.bullb.ctf.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;

/**
 * Created by oscarlaw on 6/10/2016.
 */

public class HorizontalBarView extends View {
    private float heightPercentage;


    public HorizontalBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        heightPercentage = 0;


        //Initial Angle (optional, it can be zero)
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Paint mypaint =new Paint();
//        mypaint.setAntiAlias(true);
//        String yourFormattedString = SharedUtils.addCommaToNum((float)num);
//        mypaint.setTextSize(barTextSize);//設定字體大小
//        mypaint.setColor(ContextCompat.getColor(getContext(),R.color.main_red));//設定字體顏色
//        float textWidth = mypaint.measureText(yourFormattedString);
//        if (textWidth >0) {
//            canvas.drawText(yourFormattedString, getWidth()/2 - textWidth / 2, getHeight()- (int)(barHeight*heightPercentage)+ SharedUtils.dpToPx(14) - SharedUtils.dpToPx(5), mypaint);
//        }
//        else{
//            canvas.drawText(yourFormattedString, 0, SharedUtils.dpToPx(10), mypaint);
//        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        int[] colors2 = {ContextCompat.getColor(getContext(),R.color.test_red),ContextCompat.getColor(getContext(),R.color.orange)};
        float[] positions2 = {0,1f};
        LinearGradient gradient = new LinearGradient(0,0,getWidth(), getHeight(),colors2,positions2, Shader.TileMode.MIRROR);
        paint.setShader(gradient);


        canvas.drawRect(0, 0,getWidth()*heightPercentage, getHeight(), paint);
    }

    public float getHeightPercentage() {
        return heightPercentage;
    }

    public void setHeightPercentage(float percentage) {
        this.heightPercentage = percentage;
        this.requestLayout();
    }

//    public int getBarNum() {
//        return num;
//    }
//
//    public void setBarNum(int num) {
//        this.num = num;
//        this.requestLayout();
//    }
//
//    public void setHeightPercentage(float heightPercentage,int num) {
//        this.heightPercentage = heightPercentage;
//        this.num = num;
//        this.requestLayout();
//    }




}
