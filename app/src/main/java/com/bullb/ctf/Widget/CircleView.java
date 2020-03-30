package com.bullb.ctf.Widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.R;
import com.mobsandgeeks.saripaar.annotation.Min;

/**
 * Created by oscarlaw on 5/10/2016.
 */

public class CircleView extends RelativeLayout{
    private TextView circleText, circleData,circleDataUnit, circleText2,circleData2,circleDataUnit2;
    private LinearLayout circle_data_layout, circle_data_layout2;
    private CircleFrame circleFrame;


    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.circlelayout, this);
        circleText = (TextView)findViewById(R.id.circle_description_1);
        circleData = (TextView)findViewById(R.id.circle_data_1);
        circleText2 = (TextView)findViewById(R.id.circle_description_2);
        circleData2 = (TextView)findViewById(R.id.circle_data_2);
        circle_data_layout = (LinearLayout)findViewById(R.id.data_1_layout);
        circle_data_layout2 = (LinearLayout)findViewById(R.id.data_2_layout);
        circleDataUnit = (TextView)findViewById(R.id.circle_data_1_unit);
        circleDataUnit2 = (TextView)findViewById(R.id.circle_data_2_unit);
        circleFrame = (CircleFrame)findViewById(R.id.circle_frame);

    }


    public void setData(CircleData data1, CircleData data2){
        if (data1 == null){
            circle_data_layout.setVisibility(GONE);
        }
        else{
            circleText.setText(data1.description);
            circleData.setText(data1.data);
            if (!data1.type.equals(CircleData.TYPE_MONEY) || data1.data.isEmpty()){
                circleDataUnit.setVisibility(GONE);
            }
            else{
                circleDataUnit.setVisibility(VISIBLE);
                circleDataUnit.setText(R.string.money_unit);
            }

        }

        if (data2 == null){
            circle_data_layout2.setVisibility(GONE);
        }
        else{
            circleText2.setText(data2.description);
            circleData2.setText(data2.data);
            if (!data2.type.equals(CircleData.TYPE_MONEY) || data2.data.isEmpty()){
                circleDataUnit2.setVisibility(GONE);
            }
            else{
                circleDataUnit2.setVisibility(VISIBLE);
                circleDataUnit2.setText(R.string.money_unit);
            }

        }
        requestLayout();

    }




    public void startCircleAnim(int percentage){
        int angle = Math.min(percentage*360/100, 360);

        circleFrame.setAngle(0);
        CircleAngleAnimation animation = new CircleAngleAnimation(circleFrame, angle);
        animation.setDuration(1000);
        circleFrame.startAnimation(animation);
    }

    public void setCircleAngle(int angle){
        circleFrame.setAngle(0);
        CircleAngleAnimation animation = new CircleAngleAnimation(circleFrame, angle);
        animation.setDuration(0);
        circleFrame.startAnimation(animation);
    }

}
