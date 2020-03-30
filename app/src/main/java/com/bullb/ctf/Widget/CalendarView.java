package com.bullb.ctf.Widget;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bullb.ctf.R;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by oscarlaw on 5/10/2016.
 */

public class CalendarView extends LinearLayout {
    public static int TYPE_MONTH = 0;
    public static int TYPE_QUARTER = 1;
    public static int TYPE_YEAR = 2;

    private int year,month;
    private TextView yearText, monthText;
    private ImageView backwardBtn, forwardBtn;
    private OnCalendarClickListener calendarClickListener;
    private Calendar cal;
    private int quarter = 0;
    private int specialYear = 0;
    private int type =0;

    public CalendarView(Context context) {
        super(context);
        init(null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(null);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(null);
    }

    private void init(Calendar initCal) {
        inflate(getContext(), R.layout.calendar_view_layout, this);
        backwardBtn = (ImageView)findViewById(R.id.backward_btn);
        forwardBtn = (ImageView)findViewById(R.id.forward_btn);
        monthText = (TextView)findViewById(R.id.month_text);
        yearText = (TextView)findViewById(R.id.year_text);

        if (initCal == null) {
            cal = Calendar.getInstance();
        }else {
            cal = initCal;
        }

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        quarter = getQuarterFromMonth(month);
        specialYear = getSpecialYear(year, quarter);

        if (isLast()){
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right_grey));
        }
        else{
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right));
        }

        setText();


        backwardBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackwardClick();
                if (calendarClickListener != null)
                    calendarClickListener.onBackwardClick();
            }
        });


        forwardBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLast()) {
                    onForwardClick();
                    if (calendarClickListener != null)
                        calendarClickListener.onForwardClick();
                }
            }
        });
    }

    public void setCurrentCalendar(Calendar cal){
        this.cal = cal;
        init(cal);
    }

    public void performBackwardClick(){
        backwardBtn.performClick();
    }

    public void setType(int type){
//        year = cal.get(Calendar.YEAR);
//        month = cal.get(Calendar.MONTH);

        if (type == TYPE_QUARTER){
            if (this.type == TYPE_MONTH) {
                quarter = getQuarterFromMonth(month);
            }
            specialYear = getSpecialYear(year, quarter);
        }
        else if (type == TYPE_YEAR){
            if (this.type == TYPE_MONTH) {
                specialYear = getSpecialYear(year, getQuarterFromMonth(month));
            }
        }

        this.type = type;



        if (isLast()){
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right_grey));
            setLast();
        }
        else{
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right));
        }

        setText();
//        if (cal.get(Calendar.YEAR) == year &&  quarter != 3 && quarter> getQuarterFromMonth(cal.get(Calendar.MONTH))){
//            quarter = getQuarterFromMonth(cal.get(Calendar.MONTH));
//        }
    }


    private void setLast(){
        if (type == TYPE_MONTH) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
        }
        else if (type == TYPE_QUARTER) {
            quarter = getQuarterFromMonth(cal.get(Calendar.MONTH));
            specialYear = getSpecialYear(cal.get(Calendar.YEAR), quarter);
        }
        else{
            specialYear = getSpecialYear(cal.get(Calendar.YEAR), getQuarterFromMonth(cal.get(Calendar.MONTH)));
        }
    }


    public int getType(){
        return type;
    }

    public void setText(){
        if (type == TYPE_MONTH) {
            yearText.setText(String.valueOf(year));
            String[] monthArr = getResources().getStringArray(R.array.month_arr);
            monthText.setText(monthArr[month]);
        }
        else if (type == TYPE_QUARTER){
            String[] quarterArr = getResources().getStringArray(R.array.quarter_arr);
            monthText.setText(quarterArr[quarter-1]);
            yearText.setText(String.valueOf(specialYear));
        }
        else if (type ==  TYPE_YEAR){
            yearText.setText("");
            monthText.setText(String.valueOf(specialYear));
        }
    }

    public void setCalendarClickListener(OnCalendarClickListener listener){
        calendarClickListener = listener;
    }


    public interface OnCalendarClickListener {
        void onBackwardClick();
        void onForwardClick();
    }

    public void setYear(int year){
        this.year = year;
        checkNextYear();
        if (isLast()){
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right_grey));
        }
        else{
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right));
        }
        setText();
    }

    public void setQuarter(int quarter){
        this.quarter = quarter;
        checkNextYear();
        if (isLast()){
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right_grey));
        }
        else{
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right));
        }
        setText();
    }

    public void setMonth(int month){
        this.month = month;
        checkNextYear();
        if (isLast()){
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right_grey));
        }
        else{
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right));
        }
        setText();
    }


    @CallSuper
    protected void onBackwardClick(){
        Log.d("debug","backClick");
        if (type == TYPE_MONTH) {
            month--;
        }
        else if (type == TYPE_QUARTER){
            quarter --;
        }
        else if (type == TYPE_YEAR){
            specialYear --;
            year --;
        }
        checkNextYear();
        if (isLast()){
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right_grey));
        }
        else{
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right));
        }
        setText();
    }

    @CallSuper
    protected void onForwardClick(){
        Log.d("debug","forwardClick");
        if (type == TYPE_MONTH) {
            month++;
        }
        else if (type == TYPE_QUARTER){
           quarter ++;
        }
        else if (type == TYPE_YEAR){
            year ++;
            specialYear ++;
        }
        checkNextYear();
        if (isLast()){
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right_grey));
        }
        else{
            forwardBtn.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.right));
        }
        setText();
    }

    private void checkNextYear(){
        if (type == TYPE_MONTH) {
            if (month < 0) {
                month = 11;
                year--;
            } else if (month > 11) {
                month = 0;
                year++;
            }
        }
        else if (type == TYPE_QUARTER){
            if (quarter < 1) {
                quarter = 4;
                specialYear --;
                year--;
            } else if (quarter > 4) {
                quarter = 1;
                specialYear ++;
                year++;
            }
        }
    }

    public String getStartDate() {
        String from_date = null;
        String year = String.valueOf(this.getYear());
        String month = String.valueOf(this.getMonth() + 1);

        if (type == TYPE_YEAR){
            from_date = specialYear-1 + "-" + "04-01";
        }
        else if (type == TYPE_QUARTER ){
            if (quarter == 1){
                from_date = specialYear-1 + "-" + "04-01";
            }else if (quarter ==2){
                from_date = specialYear-1 + "-" + "07-01";
            }else if (quarter ==3){
                from_date = specialYear-1 + "-" + "10-01";
            }else{
                from_date = specialYear + "-" + "01-01";
            }
        }
        else {
            if (month.length() < 2) {
                month = "0" + month;
            }
            from_date = year + "-" + month + "-01";
        }
        return from_date;
    }

    public String getEndDate(){
        String end_date = null;
        String year = String.valueOf(this.getYear());
        String month = String.valueOf(this.getMonth()+1);

        if ( type == TYPE_YEAR){
            end_date = specialYear + "-" + "03-31";
        }else if (type == TYPE_QUARTER ){
            if (quarter == 1){
                end_date = specialYear-1 + "-" + "06-30";
            }else if (quarter ==2){
                end_date = specialYear-1 + "-" + "09-30";
            }else if (quarter ==3){
                end_date = specialYear-1 + "-" + "12-31";
            }else{
                end_date = specialYear + "-" + "03-31";
            }
        }else{
            java.util.Calendar tempCal = java.util.Calendar.getInstance();
            tempCal.set(this.getYear(), this.getMonth(), 1);
            tempCal.set(Calendar.DATE, tempCal.getActualMaximum(Calendar.DATE));
            String lastDay = String.valueOf(tempCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
            if (lastDay.length() < 2) {
                lastDay = "0" + lastDay;
            }

            if (month.length() < 2) {
                month = "0" + month;
            }
            end_date = year + "-" + month + "-" + lastDay;
        }

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date date = sdf.parse(end_date);
//            Calendar temp = Calendar.getInstance();
//            temp.clear();
//            temp.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
//            if (date.after(temp.getTime())){
//                end_date = sdf.format(temp.getTime());
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        return end_date;
    }


    public boolean isLast() {
        if (type == TYPE_MONTH) {
            if (year > cal.get(Calendar.YEAR)){
                return true;
            } else if (year == cal.get(Calendar.YEAR) && month >= cal.get(Calendar.MONTH)) {
                return true;
            } else {
                return false;
            }
        } else if (type == TYPE_QUARTER) {
            if (specialYear > getSpecialYear(cal.get(Calendar.YEAR), getQuarterFromMonth(cal.get(Calendar.MONTH)))){
                return true;
            }
            else if (specialYear == getSpecialYear(cal.get(Calendar.YEAR), getQuarterFromMonth(cal.get(Calendar.MONTH))) && quarter >= getQuarterFromMonth(cal.get(Calendar.MONTH))) {
                    return true;
            }
            else{
                return false;
            }
        }
        else if (type == TYPE_YEAR){
            if (specialYear >= getSpecialYear(cal.get(Calendar.YEAR), getQuarterFromMonth(cal.get(Calendar.MONTH)))) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private int getQuarterFromMonth(int month){
        if (month == Calendar.JANUARY || month == Calendar.FEBRUARY ||month == Calendar.MARCH){
            return 4;
        }
        else if (month == Calendar.APRIL || month == Calendar.MAY || month == Calendar.JUNE){
            return 1;
        }
        else if (month == Calendar.JULY || month == Calendar.AUGUST ||month == Calendar.SEPTEMBER){
            return 2;
        }
        else {
            return 3;
        }
    }

    private int getSpecialYear(int year, int quarter){
        if (quarter != 4){
            return year + 1;
        }
        return year;
    }


    public int getQuarter(){
        return quarter;
    }

    public int getMonth(){
        return month;
    }

    public int getYear(){
        return year;
    }


}
