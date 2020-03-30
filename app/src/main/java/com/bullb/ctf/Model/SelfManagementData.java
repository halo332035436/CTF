package com.bullb.ctf.Model;


import android.content.Context;

import com.bullb.ctf.Model.Bonus;
import com.bullb.ctf.Model.CircleData;
import com.bullb.ctf.Model.Error;
import com.bullb.ctf.Model.Reward;
import com.bullb.ctf.Model.Sales;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserSale;
import com.bullb.ctf.Model.UserTarget;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class SelfManagementData extends BaseModel{
    @SerializedName("name")
    public String name;
    @SerializedName("unit")
    public String unit;
    @SerializedName("title")
    public String title;
    @SerializedName("type")
    public String type;
    @SerializedName("user_targets")
    public ArrayList<UserTarget> user_targets;
    @SerializedName("user_sales")
    public ArrayList<UserSale> user_sales;
    @SerializedName("bonuses")
    public ArrayList<Bonus> bonuses;
    @SerializedName("target")
    public double target;
    @SerializedName("decomposed_target")
    public double decomposed_target;
    @SerializedName("sales")
    public Sales sale;
    @SerializedName("last_year_sales")
    public Sales last_year_sales;
    @SerializedName("user_rewards")
    public ArrayList<Reward> user_rewards;
    @SerializedName("rewards")
    public double rewards;
}
