package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class SalesDetail implements Serializable {
    public static String TYPE_A = "A";
    public static String TYPE_F = "F";
    public static String TYPE_E = "E";


    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("type")
    public String type;
    @SerializedName("date")
    public String date;
    @SerializedName("amount")
    public double amount;


}
