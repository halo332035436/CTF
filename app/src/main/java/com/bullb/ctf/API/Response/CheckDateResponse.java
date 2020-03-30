package com.bullb.ctf.API.Response;


import com.google.gson.annotations.SerializedName;

/**
 * Created by oscar on 7/1/16.
 */
public class CheckDateResponse extends BaseResponse{
    @SerializedName("updateable")
    public boolean updateable;
    @SerializedName("confirmable")
    public boolean confirmable;
}
