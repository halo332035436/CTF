package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Descendants implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("long_description")
    public String long_description;
    @SerializedName("short_description")
    public String short_description;

}
