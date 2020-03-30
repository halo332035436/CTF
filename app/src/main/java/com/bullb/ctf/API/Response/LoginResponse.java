package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by oscar on 7/1/16.
 */
public class LoginResponse extends BaseResponse{
    @SerializedName("user")
    public User user;
    @SerializedName("api-token")
    public String apiToken;

}
