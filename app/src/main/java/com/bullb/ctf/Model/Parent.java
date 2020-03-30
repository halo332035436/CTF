package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class Parent implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("long_description")
    public String long_description;
    @SerializedName("short_description")
    public String short_description;

    public String getLongDescription() {
        if (long_description == null)
            return "";
        return long_description;
    }

    public String getShortDescription() {
        if (short_description == null)
            return "";
        return short_description;
    }
}
