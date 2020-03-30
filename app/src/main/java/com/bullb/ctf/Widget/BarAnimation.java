package com.bullb.ctf.Widget;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by oscarlaw on 6/10/2016.
 */

public class BarAnimation extends Animation {

    private BarView bar;

    private double oldHeight;
    private double newHeight;
    private double newNum;
    private double oldNum;

    public BarAnimation(BarView bar, double newHeight, double newNum) {
        this.oldHeight = bar.getHeightPercentage();
        this.newHeight = newHeight;
        this.bar = bar;
        this.newNum = newNum;
        this.oldNum = bar.getBarNum();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        double height = oldHeight + ((newHeight - oldHeight) * interpolatedTime);
        double num = oldNum + ((newNum - oldNum) * interpolatedTime);
        bar.setHeightPercentage(height,num);
    }
}