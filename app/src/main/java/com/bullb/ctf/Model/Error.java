package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class Error implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("code")
    public int code;
}
