package com.bullb.ctf.Widget;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by oscarlaw on 6/10/2016.
 */

public class CircleAngleAnimation extends Animation {

    private CircleFrame circle;

    private float oldAngle;
    private float newAngle;

    public CircleAngleAnimation(CircleFrame circle, int newAngle) {
        this.oldAngle = circle.getAngle();
        this.newAngle = newAngle;
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = oldAngle - ((newAngle - oldAngle) * interpolatedTime);

        circle.setAngle(angle);
        circle.requestLayout();
    }
}