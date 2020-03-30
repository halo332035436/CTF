package com.bullb.ctf.Widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bullb.ctf.MenuAdapter;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.LandingMenuItem;
import com.bullb.ctf.R;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

/**
 * Created by oscarlaw on 5/10/2016.
 */

public class BlurCircleView extends RelativeLayout implements View.OnClickListener{
    private boolean isShow;
    private ImageView dismissBtn;
    private CircleView circleView;

    public BlurCircleView(Context context) {
        super(context);
        if(!isInEditMode())
            init();
    }

    public BlurCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            init();
    }

    public BlurCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
            init();
    }

    private void init() {
        inflate(getContext(), R.layout.blur_view_content, this);
        isShow = false;

        //dispatchKeyEventPreIme will work only if layout is in focus
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.requestFocus();
        //disable background control
        this.setOnClickListener(this);
        this.setVisibility(GONE);


        dismissBtn = (ImageView)findViewById(R.id.dismiss_btn);
        circleView = (CircleView)findViewById(R.id.circle_view);

        dismissBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    public void setData(CircleData data1, CircleData data2){
        circleView.setData(data1, data2);
    }



    public void show(){
//        ViewPropertyAnimator.animate(this).translationY(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        this.setVisibility(VISIBLE);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.requestFocus();
        isShow = true;
    }


    public void setLocation(int[] location){

        ViewPropertyAnimator.animate(circleView).translationY(location[1]).translationX(location[0]).setDuration(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();

    }

    public void dismissImediately(){
//        ViewPropertyAnimator.animate(this).translationY(size.y).setDuration(1).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        isShow = false;
    }

    public void dismiss(){
//        ViewPropertyAnimator.animate(this).translationY(size.y).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        this.setVisibility(GONE);
        isShow = false;
    }

    public boolean isShow(){
        return isShow;
    }


    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (isShow){
                dismiss();
                return true;
            }
            // do your stuff
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View view) {

    }
}
