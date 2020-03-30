package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LeaderboardData implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("title")
    public String title;
    @SerializedName("short_description")
    public String short_description;
    @SerializedName("long_description")
    public String long_description;
    @SerializedName("sale")
    public String sale;
    @SerializedName("percentage")
    public String percentage;
    @SerializedName("icon_url")
    public String icon_url;
}
