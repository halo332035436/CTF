package com.bullb.ctf.API.Response;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class BaseResponse {
    @SerializedName("status_code")
    public String status_code;
    @SerializedName("message")
    public String error_message;
    @SerializedName("data")
    public String data;
    @SerializedName("iv")
    public String iv;
    @SerializedName("hash")
    public String hash;
    @SerializedName("timestamp")
    public String timestamp;
}
