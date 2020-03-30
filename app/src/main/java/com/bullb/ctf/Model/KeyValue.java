package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class KeyValue implements Serializable {
    @SerializedName("key")
    private String key;
    @SerializedName("value")
    private boolean value;

    public String getKey() {
        return key;
    }

    public boolean getValue() {
        return value;
    }
}
