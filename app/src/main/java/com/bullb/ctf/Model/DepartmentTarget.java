package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class DepartmentTarget implements Serializable {
    public static String TYPE_A = "A";
    public static String TYPE_F = "F";
    public static String TYPE_E = "E";


    @SerializedName("type")
    public String type;
    @SerializedName("from_date")
    public String from_date;
    @SerializedName("to_date")
    public String to_date;
    @SerializedName("amount")
    public double amount;
    @SerializedName("gross_profit")
    public double gross_profit;

}
