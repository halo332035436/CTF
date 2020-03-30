package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oscarlaw on 6/12/2016.
 */

public class Campaign {
    public static final String District = "DISTRICT";
    public static final String A = "A";
    public static final String F = "F";
    public static final String E = "E";
    public static final String M = "M";


    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
//    @SerializedName("link")
//    public String link;
    @SerializedName("image_url")
    public String image_url;
    @SerializedName("details")
    public String details;
}
