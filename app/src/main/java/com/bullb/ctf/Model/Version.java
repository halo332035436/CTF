package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Version extends BaseModel implements Serializable {

    @SerializedName("version")
    private int version;
    @SerializedName("url")
    private String url;


    public int getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }
}
