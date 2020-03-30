package com.bullb.ctf.Model;

import android.content.res.Resources;

import com.bullb.ctf.MainApplication;
import com.bullb.ctf.R;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by oscarlaw on 6/12/2016.
 */

public class Score implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("user_id")
    public String user_id;
    @SerializedName("month")
    public String month;
    @SerializedName("department_id")
    public String department_id;
    @SerializedName("kpi")
    public double kpi;
    @SerializedName("item_a")
    public String item_a;
    @SerializedName("item_b")
    public String item_b;
    @SerializedName("item_c")
    public String item_c;
    @SerializedName("descriptions")
    public Description descriptions;
    @SerializedName("user")
    public User user;

    public class Description implements Serializable{
        @SerializedName("item_a")
        public String item_a;
        @SerializedName("item_b")
        public String item_b;
        @SerializedName("item_c")
        public String item_c;

        public final String default_item_a = Resources.getSystem().getString(R.string.cs_performance);
        public final String default_item_b = Resources.getSystem().getString(R.string.team_colaboration);
        public final String default_item_c = Resources.getSystem().getString(R.string.communication_technique);
    }


    public String getUserId() {
        return user_id;
    }

    public double getKpi() {
        return kpi;
    }

    public double getItemB() {
        if (item_b == null || item_b.isEmpty())
            return 0;
        return Integer.valueOf(item_b);
    }

    public void setItemB(String s){
        item_b = s;
    }

    public int getItemA() {
        if (item_a == null || item_a.isEmpty())
            return 0;
        return Integer.valueOf(item_a);
    }

    public void setItemA(String s) {
        item_a = s;
    }

    public int getItemC() {
        if (item_c == null || item_c.isEmpty())
            return 0;
        return Integer.valueOf(item_c);
    }

    public void setItemC(String s){
        item_c = s;
    }

    public String getScore() {
        double d = getKpi() + getItemB() + (double) getItemA() + (double) getItemC();
        return String.format(Locale.CHINESE, "%.2f", d);
    }

    public String getUserName(){
        return user.name;
    }

}
