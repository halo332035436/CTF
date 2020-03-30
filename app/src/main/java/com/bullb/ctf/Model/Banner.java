package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

public class Banner {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("url")
    public String url;
    @SerializedName("image_url")
    public String image_url;
    @SerializedName("details")
    public String details;
}
