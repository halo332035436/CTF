package com.bullb.ctf.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;

/**
 * Created by oscarlaw on 6/10/2016.
 */

public class MySeekView extends View {
    private float heightPercentage;
    private Rect mColorRect;
    private int mThumbHeight = 20;
    private int mBarHeight = 2;
    private LinearGradient mColorGradient;
    private Paint mColorRectPaint;
    private int realLeft;
    private int realRight;
    private int realTop;
    private int realBottom;
    private int mBarWidth;
    private int mColorBarValue;
    private int mAlphaBarValue;
    private float mThumbRadius;
    private int mPaddingSize;
    private int[] mColors;

    public MySeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        heightPercentage = 0.3f;
        mThumbRadius = SharedUtils.dpToPx(10);
        init();

        //Initial Angle (optional, it can be zero)
    }

    private void init(){
        mThumbHeight = SharedUtils.dpToPx(20);
        mColorBarValue = 0;
        mAlphaBarValue =  0;
        mBarHeight = SharedUtils.dpToPx(10);

        realLeft = getPaddingLeft() + mPaddingSize;
        realRight = getWidth() - getPaddingRight() - mPaddingSize;
        realTop = getPaddingTop() + mPaddingSize;
        realBottom = getHeight() - getPaddingBottom() - mPaddingSize;

        //init size
        mThumbRadius = mThumbHeight / 2;
        mPaddingSize = (int) mThumbRadius;
        mBarWidth = realRight - realLeft;

        //init rect
        mColorRect = new Rect(realLeft, realTop, realRight, realTop + mBarHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        int[] colors2 = {ContextCompat.getColor(getContext(),R.color.test_red),ContextCompat.getColor(getContext(),R.color.orange)};
        float[] positions2 = {0,1f};
        LinearGradient gradient = new LinearGradient(0,0,getWidth(), getHeight(),colors2,positions2, Shader.TileMode.MIRROR);
        paint.setShader(gradient);
        canvas.drawRect(0, 0,getWidth()*heightPercentage, getHeight(), paint);

        Paint colorPaint = new Paint();
        colorPaint.setAntiAlias(true);
        colorPaint.setColor(ContextCompat.getColor(getContext(),R.color.black));

        float thumbX = getWidth()*heightPercentage;
        float thumbY = getHeight() / 2;
        canvas.drawCircle(thumbX, thumbY, SharedUtils.dpToPx(10), colorPaint);
    }

    public float getHeightPercentage() {
        return heightPercentage;
    }

    public void setHeightPercentage(float percentage) {
        this.heightPercentage = percentage;
        this.requestLayout();
    }

    private boolean isOnBar(Rect r, float x, float y) {
        if (r.left - mThumbRadius < x && x < r.right + mThumbRadius && r.top - mThumbRadius < y && y < r.bottom + mThumbRadius) {
            return true;
        } else {
            return false;
        }
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        x = event.getX();
//        y = event.getY();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (isOnBar(mColorRect, x, y)) {
//                    mMovingColorBar = true;
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                getParent().requestDisallowInterceptTouchEvent(true);
//                if (mMovingColorBar) {
//                    float value = (x - realLeft) / mBarWidth * mMaxValue;
//                    mColorBarValue = (int) value;
//                    if (mColorBarValue < 0) mColorBarValue = 0;
//                    if (mColorBarValue > mMaxValue) mColorBarValue = mMaxValue;
//                } else if (mIsShowAlphaBar) {
//                    if (mMovingAlphaBar) {
//                        float value = (x - realLeft) / mBarWidth * 255;
//                        mAlphaBarValue = (int) value;
//                        if (mAlphaBarValue < 0) mAlphaBarValue = 0;
//                        if (mAlphaBarValue > 255) mAlphaBarValue = 255;
//                        setAlphaValue();
//                    }
//                }
//                if (mOnColorChangeLister != null && (mMovingAlphaBar || mMovingColorBar))
//                    mOnColorChangeLister.onColorChangeListener(mColorBarValue, mAlphaBarValue, getColor());
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                mMovingColorBar = false;
//                break;
//        }
//        return true;
//    }





}
