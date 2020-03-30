package com.bullb.ctf.Utils;

import android.util.Log;

import java.util.Calendar;

public class Date {
    public static int TYPE_MONTH = 0;
    public static int TYPE_QUARTER = 1;
    public static int TYPE_YEAR = 2;

    private Calendar cal;
    private int year, month, quarter, specialYear, day;

    public Date() {
        this.cal = Calendar.getInstance();
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH);
        this.day = cal.get(Calendar.DATE);
        this.quarter = getQuarterFromMonth(month);
        this.specialYear = getSpecialYear(year, quarter);
    }

    public Date getAddedMonth() {
        this.addMonth();
        return this;
    }

    public void addMonth(){
        this.month++;
        if(this.month == 12){
            this.month = 0;
        }
    }

    public int getMonth(){
        return month;
    }

    public int getYear(){
        return year;
    }

    public int getDay() {return day;}

    public String getNextMonthDate(){
        String year = String.valueOf(this.getYear());
        int monthNum = this.getMonth() + 1 + 1;
        if (monthNum > 12) monthNum = 1;
        String month = String.valueOf(monthNum);
        if (month.length() < 2) {
            month = "0" + month;
        }
        return year + "-" + month + "-01";
    }

    public String getFromDate(int type){
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

    public String getToDate(int type){
        String to_date = null;
        String year = String.valueOf(this.getYear());
        String month = String.valueOf(this.getMonth() + 1);

        if ( type == TYPE_YEAR){
            to_date = specialYear + "-" + "03-31";
        }else if (type == TYPE_QUARTER ){
            if (quarter == 1){
                to_date = year + "-" + "06-30";
            }else if (quarter ==2){
                to_date = year + "-" + "09-30";
            }else if (quarter ==3){
                to_date = year + "-" + "12-31";
            }else{
                to_date = specialYear + "-" + "03-31";
            }
        }else{
            java.util.Calendar tempCal = java.util.Calendar.getInstance();
            tempCal.set(java.util.Calendar.YEAR, this.getYear());
            tempCal.set(java.util.Calendar.MONTH, this.getMonth());

            String lastDay = String.valueOf(tempCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
            if (lastDay.length() < 2) {
                lastDay = "0" + lastDay;
            }

            if (month.length() < 2) {
                month = "0" + month;
            }
            to_date = year + "-" + month + "-" + lastDay;
        }
        return to_date;
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

}
