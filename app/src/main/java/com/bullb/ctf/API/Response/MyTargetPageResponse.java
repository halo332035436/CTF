package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.BonusRate;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.Score;
import com.bullb.ctf.Model.UserSale;
import com.bullb.ctf.Model.UserTarget;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class MyTargetPageResponse extends BaseResponse implements Serializable{
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("title")
    public String title;
    @SerializedName("department")
    public Department department;
    @SerializedName("type")
    public String type;
    @SerializedName("user_targets")
    public ArrayList<UserTarget> user_targets;
    @SerializedName("user_sales")
    public ArrayList<UserSale> user_sales;
    @SerializedName("rank")
    public Ranks ranks;
    @SerializedName("bonus_rates")
    public BonusRate bonus_rates;



    public class Department implements Serializable{
        @SerializedName("id")
        public String id;
        @SerializedName("long_description")
        public String long_description;
        @SerializedName("short_description")
        public String short_description;
    }


    public Ranks getRank() {
        if (ranks == null)
            return new Ranks();
        return ranks;
    }
}
