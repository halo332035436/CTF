package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class UserTarget implements Serializable {
    public static final String TYPE_A = "A";
    public static final String TYPE_F = "F";
    public static final String TYPE_E = "E";
    public static final String TYPE_M = "M";
    public static final String TYPE_ALL = "";

    @SerializedName("id")
    public String id;
    @SerializedName("type")
    public String type;
    @SerializedName("from_date")
    public String from_date;
    @SerializedName("to_date")
    public String to_date;
    @SerializedName("base_amount")
    public double base_amount;
    @SerializedName("manager_addition")
    public double manager_addition;
    @SerializedName("user_addition")
    public double user_addition;
    @SerializedName("default_base_amount")
    public double default_base_amount;
    @SerializedName("confirmed")
    public Boolean confirmed;
}
