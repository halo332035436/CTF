package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class RewardDetail implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("amount")
    public double amount;
    @SerializedName("description")
    public String description;
    @SerializedName("date")
    public String date;
}
