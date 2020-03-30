package com.bullb.ctf.Model;

import com.bullb.ctf.API.Response.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Bonus extends BaseModel implements Serializable {
    public static String COMMISSION_PRIZE = "1";
    public static String PROFIT_PRIZE = "2";
    public static String TARGET_COMPLETE_PRIZE = "3";


    @SerializedName("type")
    public String type;
    @SerializedName("amount")
    public String amount;
    @SerializedName("description")
    public String description;



}
