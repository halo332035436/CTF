package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class Reward implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("month")
    public String month;
    @SerializedName("amount")
    public double amount;

}
