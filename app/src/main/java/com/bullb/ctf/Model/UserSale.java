package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class UserSale implements Serializable {
    public static String TYPE_A = "A";
    public static String TYPE_F = "F";
    public static String TYPE_E = "E";
    public static String TYPE_M = "M";
    public static String TYPE_ALL = "";


    @SerializedName("id")
    public String id;
    @SerializedName("user_id")
    public String user_id;
    @SerializedName("type")
    public String type;
    @SerializedName("amount")
    public double amount;
    @SerializedName("gross_profit")
    public double gross_profit;
    @SerializedName("from_date")
    public String from_date;
    @SerializedName("to_date")
    public String to_date;
    @SerializedName("sale_target_completion_rate")
    public double sale_target_completion_rate;
}
