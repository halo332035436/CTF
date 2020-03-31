package com.bullb.ctf.Widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.joooonho.SelectableRoundedImageView;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by oscarlaw on 5/10/2016.
 */

public class ProfileImageView extends RelativeLayout implements View.OnClickListener{
    private boolean isShow;
    private Point size;
    private SelectableRoundedImageView roundedImageView;

    public ProfileImageView(Context context) {
        super(context);
        init();
    }

    public ProfileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProfileImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.profile_image_layout, this);
        this.roundedImageView = (SelectableRoundedImageView)findViewById(R.id.profile_image);
    }

    public void setImage(byte[] bytes){
        Glide.with(getContext())
                .load(bytes)
                .into(roundedImageView);
    }

    public void setImage(byte[] bytes, int placeHolder){
        Glide.with(getContext())
                .load(bytes)
                .apply(new RequestOptions().placeholder(R.drawable.user_placeholder))
                .into(roundedImageView);
    }

    public void setImage(String path){
        Glide.with(getContext())
                .load(path)
                .into(roundedImageView);
    }

    public void setImage(int path){
        Glide.with(getContext())
                .load(path)
                .into(roundedImageView);
    }

    public void setMargins(int left, int top, int right, int bottom){
        FrameLayout.LayoutParams profileParmas = (FrameLayout.LayoutParams)this.getLayoutParams();
        profileParmas.setMargins(left,top,right,bottom);
        this.setLayoutParams(profileParmas);
        invalidate();
    }

    public void showAnimation(final int delay){
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewPropertyAnimator.animate(ProfileImageView.this).alpha(1).scaleX(1).scaleY(1).setStartDelay(delay).setDuration(300).start();
            }
        });
    }




    @Override
    public void onClick(View view) {

    }
}
