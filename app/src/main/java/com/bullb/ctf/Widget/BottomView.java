package com.bullb.ctf.Widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bullb.ctf.R;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by oscarlaw on 5/10/2016.
 */

public class BottomView extends RelativeLayout implements View.OnClickListener{
    private boolean isShow;
    private Point size;
    private ImageView menuBtn;
    private MenuView menuView;

    public BottomView(Context context) {
        super(context);
        init();
    }

    public BottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.bottom_layout, this);
        menuBtn = (ImageView)findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuView.isShow()) {
                    menuView.dismiss();
                }
                else{
                    menuView.show();

                }

            }
        });
    }

    public void setMenuView(MenuView menuView){
        this.menuView = menuView;

    }



    @Override
    public void onClick(View view) {

    }
}
