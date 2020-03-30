package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.Error;
import com.google.gson.annotations.SerializedName;

/**
 * Created by oscar on 7/1/16.
 */
public class ErrorResponse {
    @SerializedName("error")
    public Error error;
}
