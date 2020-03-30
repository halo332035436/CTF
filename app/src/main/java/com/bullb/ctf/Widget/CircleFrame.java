package com.bullb.ctf.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;

/**
 * Created by oscarlaw on 6/10/2016.
 */

public class CircleFrame extends View {

    private static final int START_ANGLE_POINT = 270;

    private final Paint paint;
    private final RectF rect;

    private float angle;

    public CircleFrame(Context context, AttributeSet attrs) {
        super(context, attrs);



        final int strokeWidth = (int) getResources().getDimension(R.dimen.circle_stroke_width);
        final int size = (int) getResources().getDimension(R.dimen.circle_width) - 2* strokeWidth;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        //Circle color
//        paint.setColor(ContextCompat.getColor(context, R.color.main_red));
//        int[] colors = {ContextCompat.getColor(context,R.color.percent_20), ContextCompat.getColor(context,R.color.percent_40),ContextCompat.getColor(context,R.color.percent_60),ContextCompat.getColor(context,R.color.percent_80),ContextCompat.getColor(context,R.color.percent_100)};
//        float[] positions = {0,0.25f,0.5f,0.75f,0.1f};

//        int[] colors2 = {ContextCompat.getColor(context,R.color.test_red),ContextCompat.getColor(context,R.color.orange)};
        int[] colors2 = {ContextCompat.getColor(context,R.color.orange),ContextCompat.getColor(context,R.color.orange)};

        float[] positions2 = {0,1f};

        SweepGradient gradient = new SweepGradient(size/2 + strokeWidth,size/2 + strokeWidth ,colors2, positions2);
        paint.setShader(gradient);

        float rotate = 270f;
        Matrix gradientMatrix = new Matrix();
        gradientMatrix.preRotate(rotate, size/2 + strokeWidth, size/2 + strokeWidth);
        gradient.setLocalMatrix(gradientMatrix);
        paint.setShader(gradient);

        rect = new RectF(strokeWidth, strokeWidth, size + strokeWidth, size + strokeWidth);

        //Initial Angle (optional, it can be zero)
        angle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }


}
